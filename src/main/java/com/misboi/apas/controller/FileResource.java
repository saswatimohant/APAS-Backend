package com.misboi.apas.controller;


import com.misboi.apas.entities.AuthDetails;
import com.misboi.apas.entities.DatabaseFile;
import com.misboi.apas.helper.ResourceNotFoundException;
import com.misboi.apas.repository.DatabaseFileRepository;
import com.misboi.apas.services.impl.DruAuthServiceImpl;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static com.misboi.apas.entities.AuthDetails.*;
import static java.nio.file.Files.copy;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

@RestController
@RequestMapping("/file")
public class FileResource {

    @Autowired
    private DatabaseFileRepository dbFileRepo;

    @Autowired
    private DruAuthServiceImpl druAuthService;


private static final Logger log = LoggerFactory.getLogger(FileResource.class);


    // define a location to save the file on server
    // Here the downloads folder and uploads should exist in your system local storage
    public static final String DIRECTORY = System.getProperty("user.home") +"/Downloads/uploads/";


    public FileResource() throws ParseException {
    }

    // define a function to upload files
    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadFiles(@RequestParam("files")List<MultipartFile> multipartFiles) throws Exception {
        List<String> fileNames = new ArrayList<>();
        for(MultipartFile file : multipartFiles)
        {
            // loop through the multipartFiles and save on this system
            String filename = StringUtils.cleanPath(file.getOriginalFilename());
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/download/")
                    .path(file.getOriginalFilename())
                    .toUriString();
            System.out.println("File URI: "+fileDownloadUri);
            Path fileStorage = get(DIRECTORY, filename).toAbsolutePath().normalize();
            System.out.println("File path: "+fileStorage);
            filepath = String.valueOf(fileStorage);
            copy(file.getInputStream(),fileStorage,REPLACE_EXISTING);
            fileNames.add(filename);
            System.out.println(fileDownloadUri);
            JSONObject processResponse= druAuthService.processDocument(docid, String.valueOf(fileStorage),SESSIONTOKEN,processurl);
            log.debug("Process Response :"+processResponse);
            DRUSIGHT_DATA = processResponse;
            DatabaseFile dbFile = new DatabaseFile(filename,file.getContentType(),DRUSIGHT_DATA);
            dbFileRepo.save(dbFile);
        }
        return ResponseEntity.ok().body(fileNames);
    }
//    @GetMapping("/processDoc")
//    public ResponseEntity<JSONObject> process(String filename) throws Exception {
//        JSONObject processResponse= druAuthService.processDocument(docid,AuthDetails.filepath,SESSIONTOKEN,processurl);
//        log.debug("Process Response :"+processResponse);
//        DRUSIGHT_DATA = processResponse;
//        return ResponseEntity.ok().body(processResponse);
//    }

    @GetMapping("/listAll/{id}")
    public ResponseEntity<DatabaseFile> getFileById(@PathVariable long id){
        DatabaseFile dbFile = dbFileRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("File not found with the id: "+id));
        return ResponseEntity.ok(dbFile);
    }



    @GetMapping("/listAll")
    public List<DatabaseFile> getAllFiles(){
        return dbFileRepo.findAll();
    }


    // define a method to download files

    @GetMapping("download/{filename}")
    public ResponseEntity<Resource> downloadFiles(@PathVariable("filename")String filename) throws IOException {
        Path filePath = get(DIRECTORY).toAbsolutePath().normalize().resolve(filename);
        if(!Files.exists(filePath))
        {
            throw new FileNotFoundException(filename + " was not found on the server");
        }
        Resource resource = new UrlResource(filePath.toUri());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("File-Name",filename);
        httpHeaders.add(CONTENT_DISPOSITION,"inline;File-Name=\""+resource.getFilename());
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(Files.probeContentType(filePath)))
                .headers(httpHeaders).body(resource);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<DatabaseFile> updateFileDetails(@PathVariable Long id, @RequestBody DatabaseFile databaseFile){
        DatabaseFile file = dbFileRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("File not exists with id: "+id));

        file.setFileName(databaseFile.getFileName());
        file.setFileType(databaseFile.getFileType());
        file.setData(databaseFile.getData());

        DatabaseFile updatedFile = dbFileRepo.save(file);
        return ResponseEntity.ok(updatedFile);
    }

    // api to delete file
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteFile(@PathVariable Long id){
        DatabaseFile file = dbFileRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("File not found with id: "+id));
        dbFileRepo.delete(file);
        Map<String, Boolean> response = new HashMap<>();
        response.put("Deleted", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }
}