package puzzlemaker.model;

import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import puzzlemaker.tools.WordCluePair;

public class WordCluePairCompareTest {

	ArrayList<WordCluePair> m_data;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		m_data = new ArrayList<WordCluePair>();
		m_data.add(new WordCluePair("ONE"));
		m_data.add(new WordCluePair("two"));
		m_data.add(new WordCluePair("three"));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		System.out.println(m_data);
		if (m_data.remove(new WordCluePair("two"))) {
			System.out.println("Success!");
		}
		else {
			fail("Fail 1.");
		}
		System.out.println(m_data);
	}

}
