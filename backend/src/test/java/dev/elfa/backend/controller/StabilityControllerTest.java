package dev.elfa.backend.controller;

import com.mongodb.client.gridfs.model.GridFSFile;
import dev.elfa.backend.repository.InfluencerRepo;
import dev.elfa.backend.service.GridFsService;
import okhttp3.mockwebserver.MockWebServer;
import org.bson.BsonObjectId;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class StabilityControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private GridFsService mockGridFsService;

    @MockBean
    private InfluencerRepo mockInfluencerRepo;

    @MockBean
    private GridFsTemplate mockGridFsTemplate;

    private static final MockWebServer mockWebServer = new MockWebServer();

    @BeforeAll
    static void setup() throws IOException {
        mockWebServer.start();
    }

    @AfterAll
    static void shutDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void getBaseImage_ValidId_ReturnsOkStatus() throws Exception {
        Document metadata = new Document("1", new ByteArrayInputStream(new byte[0]));
        GridFSFile file = new GridFSFile(new BsonObjectId(), "foo", 0, 0, new Date(), metadata);
        GridFsResource resource = new GridFsResource(file);

        when(mockGridFsService.getBaseImage("1000")).thenReturn(resource);

        mvc.perform(MockMvcRequestBuilders.get("/api/stability/influencer/1000/base-image"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("image/webp"));
    }
}