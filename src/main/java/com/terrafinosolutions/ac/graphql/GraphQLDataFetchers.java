package com.terrafinosolutions.ac.graphql;

import com.google.common.collect.ImmutableMap;
import graphql.schema.DataFetcher;
import io.terrafino.api.ac.AcException;
import io.terrafino.api.ac.ado.Ado;
import io.terrafino.api.ac.timeseries.TsRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class GraphQLDataFetchers {

    @Autowired
    private AcAccess ac;

    public DataFetcher<List<Map<String, String>>> getFxRatesByBaseCurrency() {
        return dataFetchingEnvironment -> {
            String ccy = dataFetchingEnvironment.getArgument("baseCurrency");
            return ac.findAdosByBaseCurrency(ccy).stream().map(ado -> getAdoData(ado)).collect(Collectors.toList());
        };
    }

    private Map<String, String> getAdoData(Ado ado) {
        return ImmutableMap.of(
                "id", ado.getId(),
                "name", ado.getLongname(),
                "baseCurrency", getAsString(ado, "C0#SA010"),
                "quoteCurrency", getAsString(ado, "C0#SA011")
        );
    }

    private String getAsString(Ado ado, String attr) {
        try {
            return ado.load(attr).toString();
        } catch (AcException e) {
            return "<error>";
        }
    }

    public DataFetcher<List<TimeseriesRow>> getTimeseriesDataFetcher() {
        return dataFetchingEnvironment -> {
            Map<String, Object> ado = dataFetchingEnvironment.getSource();
            String adoId = (String) ado.get("id");
            List<TsRecord> records = ac.getTimeseriesFor(adoId, "CONSOLIDATION_C0");
            return records.stream().map(record ->
                    new TimeseriesRow(record.getDate(), record.get(0).toDouble())
            ).collect(Collectors.toList());
        };
    }
}

