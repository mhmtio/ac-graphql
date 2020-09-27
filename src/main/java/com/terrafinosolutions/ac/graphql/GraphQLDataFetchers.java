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

    public DataFetcher<Map<String, String>> getAdoByIdDataFetcher() {
        return dataFetchingEnvironment -> {
            String adoId = dataFetchingEnvironment.getArgument("id");
            return ac.findAdoById(adoId).map(ado -> getAdoData(ado)).orElse(null);
        };
    }

    private Map<String, String> getAdoData(Ado ado) {
        return ImmutableMap.of(
                "id", ado.getId(),
                "name", ado.getLongname(),
                "ccy1", getAsString(ado, "C0#SA010"),
                "ccy2", getAsString(ado, "C0#SA011")
        );
    }

    private String getAsString(Ado ado, String attr) {
        try {
            return ado.load(attr).toString();
        } catch (AcException e) {
            return "<error>";
        }
    }

    public DataFetcher<List<Map<String, String>>> getAdoByCCy1DataFetcher() {
        return dataFetchingEnvironment -> {
            String ccy1 = dataFetchingEnvironment.getArgument("ccy1");
            return ac.findAdosByCcy1(ccy1).stream().map(ado -> getAdoData(ado)).collect(Collectors.toList());
        };
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

