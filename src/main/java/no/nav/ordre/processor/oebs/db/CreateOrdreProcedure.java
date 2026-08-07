package no.nav.ordre.processor.oebs.db;

import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.ordre.Ordre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Map;

@Slf4j
@Repository
public class CreateOrdreProcedure {

    private static final String SCHEMA = "xxrtv";
    private static final String PACKAGE = "xxrtv_digihot_ont_api_pkg";
    private static final String PROCEDURE = "digihotordreontinfo";

    private static final String IN_PARAM_ORGID = "p_json_melding"; //todo: change to input variables
    private static final String OUT_PARAM_VALID = "p_retur_melding";

    private final SimpleJdbcCall validateKontostrengCall;

    @Autowired
    public CreateOrdreProcedure(DataSource dataSource) {
        this.validateKontostrengCall = new SimpleJdbcCall(dataSource)
                .withSchemaName(SCHEMA)
                .withCatalogName(PACKAGE)
                .withProcedureName(PROCEDURE)
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter(IN_PARAM_ORGID, Types.NUMERIC),
                        new SqlOutParameter(OUT_PARAM_VALID, Types.VARCHAR)
                );
    }

    public boolean executeCreateOrdre(Ordre ordre) {
        Map<String, Object> result = createOrdre(ordre);
        String resultMessage = (String) result.get(OUT_PARAM_VALID);
        log.info("Executing procedure with result valid={}", resultMessage);
        return true;
    }

    public Map<String, Object> createOrdre(Ordre ordre) {
        MapSqlParameterSource inputParams = new MapSqlParameterSource()
                .addValue("p_fodelsnummer", ordre.getFodselNummer())
                .addValue(IN_PARAM_ORGID, ordre.getOrdreType());
        return validateKontostrengCall.execute(inputParams);
    }
}
