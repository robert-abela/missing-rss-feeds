package com.robertabela.rss;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.robertabela.rss.lidl.Offers;
import com.rometools.rome.feed.rss.Item;

@RunWith(SpringRunner.class)
@WebMvcTest(value = RssFeedController.class, secure = false)
public class RssFeedControllerTest {
	
	private static List<Item> mockItems = new ArrayList<>();
	
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private Offers lidlOffers;

	@BeforeClass 
	public static void setup() {
		Item fake = new Item();
		fake.setTitle("Fake title");
		fake.setLink("http://fake.com/fake-item");
		mockItems.add(fake);
	}
	
	@Test
	public void retrieveDetailsForCourse() throws Exception {

		Mockito.when(
				lidlOffers.getProducts()).thenReturn(mockItems);

		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.get("/lidl")
				.accept(MediaType.APPLICATION_XML);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		System.out.println(result.getResponse());
		String expectedTitle = "<title>fake title</title>";


		assertTrue(result.getResponse().getContentAsString().contains(expectedTitle));
	}

}

