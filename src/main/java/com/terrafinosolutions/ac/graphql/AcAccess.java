package com.terrafinosolutions.ac.graphql;

import io.terrafino.api.ac.AcException;
import io.terrafino.api.ac.ado.Ado;
import io.terrafino.api.ac.attribute.Attributes;
import io.terrafino.api.ac.service.AcConnection;
import io.terrafino.api.ac.service.AcQueryService;
import io.terrafino.api.ac.service.AcService;
import io.terrafino.api.ac.timeseries.TsRecord;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class AcAccess {

    private AcConnection conn;
    private AcService ac;
    private AcQueryService acQueryService;

    public AcAccess() {
        try {
            this.conn = AcConnection.getDefaultConnection();
            this.ac = new AcService(conn);
            this.acQueryService = new AcQueryService(conn);
        } catch (AcException e) {
            // ...
        }
    }

    public Optional<Ado> findAdoById(String id) throws AcException {
        List<Ado> ados = acQueryService.adoBrowse(String.format("symbol = '%s'", id));
        if (ados.size() == 1) {
            return Optional.of(ados.get(0));
        } else {
            return Optional.empty();
        }
    }

    public List<Ado> findAdosByCcy1(String ccy1) throws AcException {
        return acQueryService.adoBrowse(
                String.format("symbol like 'C0.FXS%%' and attribute('C0#SA010') = '%s' ", ccy1));
    }

    public List<TsRecord> getTimeseriesFor(String adoId, String tree) throws AcException {
        return acQueryService.loadTimeseries(ac.createAdo(adoId), tree, 0, 0,
                Attributes.attributes("CLOSE")).getRecords();
    }
}
