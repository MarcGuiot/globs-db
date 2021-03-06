package org.globsframework.sqlstreams;

import org.globsframework.metamodel.GlobModel;
import org.globsframework.sqlstreams.model.DummyObject;
import org.globsframework.sqlstreams.model.DummyObject2;
import org.globsframework.sqlstreams.model.GlobChecker;
import org.globsframework.model.GlobRepository;
import org.globsframework.sqlstreams.constraints.Constraints;
import org.globsframework.sqlstreams.drivers.jdbc.DbServicesTestCase;
import org.globsframework.xml.XmlGlobStreamReader;
import org.junit.Test;

public class SqlGlobRepositoryBuilderTest extends DbServicesTestCase {

    @Test
    public void testNominal() throws Exception {
        populate(sqlConnection, XmlGlobStreamReader.parse(
                "<dummyObject id='1' name='name' value='1.1' present='true'/>" +
                        "<dummyObject id='4' name='word' value='3.3' present='false'/>", directory.get(GlobModel.class)));
        populate(sqlConnection, XmlGlobStreamReader.parse(
                "<dummyObject2 id='3' label='label'/>" +
                        "<dummyObject2 id='4' label='label'/>", directory.get(GlobModel.class)));


        SqlGlobRepositoryBuilder repositoryBuilder = SqlGlobRepositoryBuilder.init(sqlService);
        repositoryBuilder.add(Constraints.equal(DummyObject.NAME, "name"),
                DummyObject.VALUE, DummyObject.DATE);
        repositoryBuilder.add(Constraints.and(Constraints.equal(DummyObject2.LABEL, "label"),
                Constraints.strictlyGreater(DummyObject2.ID, 3)),
                DummyObject2.ID, DummyObject2.LABEL);
        GlobRepository repository = repositoryBuilder.getGlobRepository();

        GlobChecker checker = new GlobChecker(globModel);
        checker.assertEquals(repository, "<dummyObject2 id='4' label='label'/>" +
                "<dummyObject id='1' value='1.1'/>");

    }
}
