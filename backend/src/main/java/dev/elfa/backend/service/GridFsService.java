package dev.elfa.backend.service;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.client.gridfs.model.GridFSFile;
import dev.elfa.backend.model.FileMetadata;
import dev.elfa.backend.model.Influencer;
import dev.elfa.backend.repository.InfluencerRepo;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GridFsService {
    public static final String ACCOUNT_ID = "accountId";
    public static final String APPROVED = "approved";
    private final GridFsTemplate gridFsTemplate;
    private final InfluencerRepo influencerRepo;

    public FileMetadata saveImage(Resource image, String twitterId) throws IOException {
        if (!image.exists()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");

        InputStream inputStream = image.getInputStream();

        ObjectId objectId = gridFsTemplate.store(
                inputStream,
                UUID.randomUUID().toString(),
                "image/webp",
                BasicDBObjectBuilder.start().add(ACCOUNT_ID, twitterId).get()
        );

        return getFileMetadata(objectId.toString());
    }

    public FileMetadata saveBaseImage(Resource image, String twitterId) throws IOException {
        if (!image.exists()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");

        InputStream inputStream = image.getInputStream();

        ObjectId objectId = gridFsTemplate.store(
                inputStream,
                UUID.randomUUID().toString(),
                "image/webp",
                BasicDBObjectBuilder.start().add(ACCOUNT_ID, twitterId).add(APPROVED, true).get()
        );

        return getFileMetadata(objectId.toString());
    }

    public GridFsResource getResource(String id) {
        return gridFsTemplate.getResource(getFile(id));
    }

    public FileMetadata getFileMetadata(String id) {
        GridFSFile gridFSFile = getFile(id);

        Document metadata = Optional
                .ofNullable(gridFSFile.getMetadata())
                .orElse(new Document(Map.of("_contentType", "", ACCOUNT_ID, "", APPROVED, false)));

        return new FileMetadata(
                id,
                gridFSFile.getFilename(),
                metadata.getString("_contentType"),
                gridFSFile.getLength(),
                metadata.getString(ACCOUNT_ID)
        );
    }

    public GridFSFile getFile(String id) {
        return Optional
                .ofNullable(gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(id))))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));
    }

    public GridFsResource getBaseImage(String id) {
        Optional<Influencer> influencer = influencerRepo.findById(id);
        if (influencer.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Influencer does not exist.");

        String baseImageId = influencer.get().getImage().baseImage();

        return getResource(baseImageId);
    }
}
