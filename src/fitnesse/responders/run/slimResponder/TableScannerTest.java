package fitnesse.responders.run.slimResponder;

import fitnesse.wiki.InMemoryPage;
import fitnesse.wiki.WikiPage;
import fitnesse.wiki.WikiPageUtil;
import fitnesse.wiki.PageData;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class TableScannerTest {
  private WikiPage root;

  @Before
  public void setUp() throws Exception {
    root = InMemoryPage.makeRoot("root");
  }

  @Test
  public void noTables() throws Exception {
    PageData data = root.getData();
    data.setContent("");
    TableScanner ts = new TableScanner(data);
    assertEquals(0, ts.getTableCount());
  }

  @Test
  public void oneRowTable() throws Exception {
    WikiPageUtil.setPageContents(root, "|x|\n");
    checkOneRowTable();
  }

  private void checkOneRowTable() throws Exception {
    PageData data = root.getData();
    TableScanner ts = new TableScanner(data);
    assertEquals(1, ts.getTableCount());
    Table t = ts.getTable(0);
    assertEquals(1, t.getRowCount());
    assertEquals(1, t.getColumnCountInRow(0));
    assertEquals("x", t.getCellContents(0, 0));
  }

  @Test
  public void oneRowTableWithGunkAroundIt() throws Exception {
    WikiPageUtil.setPageContents(root, "gunk\n|x|\ngunk");
    checkOneRowTable();
  }


  @Test
  public void twoOneRowTables() throws Exception {
    WikiPageUtil.setPageContents(root, "|x|\n\n|y|\n");
    TableScanner ts = new TableScanner(root.getData());
    assertEquals(2, ts.getTableCount());
    Table t1 = ts.getTable(0);
    assertEquals(1, t1.getRowCount());
    assertEquals("x", t1.getCellContents(0, 0));
    Table t2 = ts.getTable(1);
    assertEquals(1, t2.getRowCount());
    assertEquals("y", t2.getCellContents(0, 0));
  }

  @Test
  public void twoByTwoTable() throws Exception {
    WikiPageUtil.setPageContents(root, "|a|b|\n|c|d|\n");
    TableScanner ts = new TableScanner(root.getData());
    assertEquals(1, ts.getTableCount());
    Table t = ts.getTable(0);
    assertEquals(2, t.getRowCount());
    assertEquals(2, t.getColumnCountInRow(0));
    assertEquals(2, t.getColumnCountInRow(1));
    assertEquals("a", t.getCellContents(0, 0));
    assertEquals("b", t.getCellContents(1, 0));
    assertEquals("c", t.getCellContents(0, 1));
    assertEquals("d", t.getCellContents(1, 1));
  }

  @Test
  public void canParseMoreThanOneTable() throws Exception {
    WikiPageUtil.setPageContents(root, "junk\n|a|b|\njunk\n|c|d|\njunk\n");
    TableScanner ts = new TableScanner(root.getData());
    assertEquals(2, ts.getTableCount());
  }


  @Test
  public void canDumpTablesBackToWikiText() throws Exception {
    String contents = "junk\n|a|b|\njunk\n|c|d|\njunk\n";
    WikiPageUtil.setPageContents(root, contents);
    TableScanner ts = new TableScanner(root.getData());
    assertEquals(contents, ts.toWikiText());
  }


}