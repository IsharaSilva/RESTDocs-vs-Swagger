package com.example.demo.controllerTest;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcConfigurationCustomizer;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentationConfigurer;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.demo.controller.SupplierController;
import com.example.demo.dto.SupplierInputDTO;
import com.example.demo.dto.SupplierOutputDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.SupplierRepository;
import com.example.demo.service.SupplierService;

@RunWith(SpringRunner.class)
@WebMvcTest(SupplierController.class)
@AutoConfigureRestDocs(outputDir = "target/snippets")
public class SupplierControllerTest {

	@Autowired
	private MockMvc mockmvc;

	@InjectMocks
	private SupplierController supplierController;

	@MockBean
	private SupplierService supplierService;

	@MockBean
	private SupplierRepository supplierRepository;

	@TestConfiguration
	static class CustomizationConfiguration implements RestDocsMockMvcConfigurationCustomizer {

		@Override
		public void customize(MockMvcRestDocumentationConfigurer configurer) {
			configurer.operationPreprocessors().withRequestDefaults(prettyPrint()).withResponseDefaults(prettyPrint());
		}

		@Bean
		public RestDocumentationResultHandler restDocumentation() {
			return MockMvcRestDocumentation.document("{method-name}");
		}
	}

	private static final String SUPPLIER_PATH = "/api/suppliers";
	private static final String SUPPLIER_WITH_ID_PATH = "/api/suppliers/{id}";
	public static final Long SUPPLIER_ID1 = 1L;
	public static final String SUPPLIER_NAME1 = "Supplier1";
	public static final String SUPPLIER_ADDRESS1 = "469";
	public static final String SUPPLIER_CONTACT1 = "07744876755";
	public static final String SUPPLIER_SPECIALTIES1 = "hardware";
	public static final Long SUPPLIER_ID2 = 2L;
	public static final String SUPPLIER_NAME2 = "Supplier2";
	public static final String SUPPLIER_ADDRESS2 = "235";
	public static final String SUPPLIER_CONTACT2 = "07844876755";
	public static final String SUPPLIER_SPECIALTIES2 = "software";
	public static final String SUPPLIER_ID_DESCRIPTION = "Supplier's ID";
	public static final String SUPPLIER_NAME_DESCRIPTION = "Supplier's Name";
	public static final String SUPPLIER_ADDRESS_DESCRIPTION ="Supplier's Address";
	public static final String SUPPLIER_CONTACT_DESCRIPTION = "Supplier's Contact Details";
	public static final String SUPPLIER_SPECIALTIES_DESCRIPTION = "Supplier's Specialties";
	public static final Long NON_EXISTING_SUPPLIER_ID = 999L;
	public static final String UPDATED_SUPPLIER_NAME = "UpdatedSupplier";
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String ADDRESS ="address";
	public static final String CONTACT = "contactDetails";
	public static final String SPECIALTIES = "specialties";
	
	public static SupplierOutputDTO supplier1;
	public static SupplierOutputDTO supplier2;
	public static SupplierOutputDTO createdSupplierOutputDTO;
	public static SupplierInputDTO mockSupplierInputDTO;
	public static SupplierOutputDTO updatedSupplierOutputDTO;
	
	@BeforeEach
	public void setup() {
		supplier1 = new SupplierOutputDTO(SUPPLIER_ID1, SUPPLIER_NAME1, SUPPLIER_ADDRESS1, SUPPLIER_CONTACT1, SUPPLIER_SPECIALTIES1);
		supplier2 = new SupplierOutputDTO(SUPPLIER_ID2, SUPPLIER_NAME2, SUPPLIER_ADDRESS2, SUPPLIER_CONTACT2, SUPPLIER_SPECIALTIES2);
		createdSupplierOutputDTO = new SupplierOutputDTO(SUPPLIER_ID2, SUPPLIER_NAME2, SUPPLIER_ADDRESS2, SUPPLIER_CONTACT2, SUPPLIER_SPECIALTIES2);
		mockSupplierInputDTO = new SupplierInputDTO(UPDATED_SUPPLIER_NAME, SUPPLIER_ADDRESS1, SUPPLIER_CONTACT1,
				SUPPLIER_SPECIALTIES1);

		updatedSupplierOutputDTO = new SupplierOutputDTO(SUPPLIER_ID1, UPDATED_SUPPLIER_NAME, SUPPLIER_ADDRESS1, SUPPLIER_CONTACT1,
				SUPPLIER_SPECIALTIES1);
	}
	
	@Test
	public void testGetAllSuppliers() throws Exception {

		List<SupplierOutputDTO> expectedSuppliers = Arrays.asList(supplier1, supplier2);

		when(supplierService.getAllSuppliers()).thenReturn(expectedSuppliers);

		mockmvc.perform(get(SUPPLIER_PATH + "/")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].name", is(SUPPLIER_NAME1))).andExpect(jsonPath("$[1].name", is(SUPPLIER_NAME2)))
				.andExpect(jsonPath("$[0].address", is(SUPPLIER_ADDRESS1))).andExpect(jsonPath("$[1].address", is(SUPPLIER_ADDRESS2)))
				.andExpect(jsonPath("$[0].contactDetails", is(SUPPLIER_CONTACT1)))
				.andExpect(jsonPath("$[1].contactDetails", is(SUPPLIER_CONTACT2)))
				.andExpect(jsonPath("$[0].specialties", is(SUPPLIER_SPECIALTIES1)))
				.andExpect(jsonPath("$[1].specialties", is(SUPPLIER_SPECIALTIES2)))
				.andDo(document("{methodName}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
						responseFields(fieldWithPath("[].id").description(SUPPLIER_ID_DESCRIPTION),
								fieldWithPath("[].name").description(SUPPLIER_NAME_DESCRIPTION),
								fieldWithPath("[].address").description(SUPPLIER_NAME_DESCRIPTION),
								fieldWithPath("[].contactDetails").description(SUPPLIER_CONTACT_DESCRIPTION),
								fieldWithPath("[].specialties").description(SUPPLIER_SPECIALTIES_DESCRIPTION))));
	}

	@Test
	public void testGetSupplierByIdExisting() throws Exception {
		
		when(supplierService.getSupplierById(anyLong())).thenReturn(supplier1);

		mockmvc.perform(MockMvcRequestBuilders.get(SUPPLIER_WITH_ID_PATH, SUPPLIER_ID1).contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(SUPPLIER_ID1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(SUPPLIER_NAME1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.address").value(SUPPLIER_ADDRESS1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.contactDetails").value(SUPPLIER_CONTACT1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.specialties").value(SUPPLIER_SPECIALTIES1))
				.andDo(document("{methodName}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
						responseFields(fieldWithPath(ID).description(SUPPLIER_ID_DESCRIPTION),
								fieldWithPath(NAME).description(SUPPLIER_NAME_DESCRIPTION),
								fieldWithPath(ADDRESS).description(SUPPLIER_ADDRESS_DESCRIPTION),
								fieldWithPath(CONTACT).description(SUPPLIER_CONTACT_DESCRIPTION),
								fieldWithPath(SPECIALTIES).description(SUPPLIER_SPECIALTIES_DESCRIPTION))));
	}

	@Test
	public void testGetSupplierByIdNonExisting() throws Exception {

	    when(supplierService.getSupplierById(anyLong()))
        .thenThrow(new ResourceNotFoundException("No Supplier entity with id " + NON_EXISTING_SUPPLIER_ID + " exists!"));

	    mockmvc.perform(MockMvcRequestBuilders.get(SUPPLIER_WITH_ID_PATH, NON_EXISTING_SUPPLIER_ID)
	            .contentType(MediaType.APPLICATION_JSON))
	            .andExpect(MockMvcResultMatchers.status().isNotFound())
	            .andDo(document("{methodName}",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint())));
	}

	@Test
	public void testCreateSupplier() throws Exception {
		
		when(supplierService.createSupplier(any(SupplierInputDTO.class))).thenReturn(createdSupplierOutputDTO);

		mockmvc.perform(MockMvcRequestBuilders.post(SUPPLIER_PATH).contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"NewSupplier\"}")).andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(SUPPLIER_ID2))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(SUPPLIER_NAME2))
				.andExpect(MockMvcResultMatchers.jsonPath("$.address").value(SUPPLIER_ADDRESS2))
				.andExpect(MockMvcResultMatchers.jsonPath("$.contactDetails").value(SUPPLIER_CONTACT2))
				.andExpect(MockMvcResultMatchers.jsonPath("$.specialties").value(SUPPLIER_SPECIALTIES2))
				.andDo(document("{methodName}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
						responseFields(fieldWithPath(ID).description(SUPPLIER_ID_DESCRIPTION),
								fieldWithPath(NAME).description(SUPPLIER_NAME_DESCRIPTION),
								fieldWithPath(ADDRESS).description(SUPPLIER_ADDRESS_DESCRIPTION),
								fieldWithPath(CONTACT).description(SUPPLIER_CONTACT_DESCRIPTION),
								fieldWithPath(SPECIALTIES).description(SUPPLIER_SPECIALTIES_DESCRIPTION))));
	}

	@Test
	public void testUpdateSupplier() throws Exception {

		when(supplierService.updateSupplier(anyLong(), any(SupplierInputDTO.class)))
				.thenReturn(updatedSupplierOutputDTO);

		mockmvc.perform(MockMvcRequestBuilders.put(SUPPLIER_WITH_ID_PATH, 1L).contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"UpdatedSupplier\"}")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(SUPPLIER_ID1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(UPDATED_SUPPLIER_NAME))
				.andExpect(MockMvcResultMatchers.jsonPath("$.address").value(SUPPLIER_ADDRESS1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.contactDetails").value(SUPPLIER_CONTACT1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.specialties").value(SUPPLIER_SPECIALTIES1))
				.andDo(document("{methodName}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
						responseFields(fieldWithPath(ID).description(SUPPLIER_ID_DESCRIPTION),
								fieldWithPath(NAME).description(SUPPLIER_NAME_DESCRIPTION),
								fieldWithPath(ADDRESS).description(SUPPLIER_ADDRESS_DESCRIPTION),
								fieldWithPath(CONTACT).description(SUPPLIER_CONTACT_DESCRIPTION),
								fieldWithPath(SPECIALTIES).description(SUPPLIER_SPECIALTIES_DESCRIPTION))));
	}

	@Test
	public void testDeleteSupplier() throws Exception {
		doNothing().when(supplierService).deleteSupplier(anyLong());

		mockmvc.perform(
				MockMvcRequestBuilders.delete(SUPPLIER_WITH_ID_PATH, SUPPLIER_ID1).contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNoContent())
				.andDo(document("{methodName}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
	}

	@Test
	public void testDeleteNonExistingSupplier() throws Exception {
		doNothing().when(supplierService).deleteSupplier(NON_EXISTING_SUPPLIER_ID);

		mockmvc.perform(
				MockMvcRequestBuilders.delete(SUPPLIER_WITH_ID_PATH, NON_EXISTING_SUPPLIER_ID).contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNoContent())
				.andDo(document("{methodName}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
	}
}