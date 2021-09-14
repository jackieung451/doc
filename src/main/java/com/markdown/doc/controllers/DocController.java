package com.markdown.doc.controllers;

import com.markdown.doc.dtos.DocDTO;
import com.markdown.doc.exceptions.UserNotAllowedException;
import com.markdown.doc.services.DocService;
import com.markdown.doc.services.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/doc")
public class DocController {

    @Autowired
    DocService docService;

    @Autowired
    TokenService tokenService;

    //    create his own documents
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public DocDTO createDocument(@RequestBody DocDTO docDTO) {
//    TODO: create service that handle creation logic
        docService.createDocument(docDTO);
    return docDTO;
    }
//    fetch his own documents
@GetMapping("/{userId}/all")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public List<DocDTO> fetchUserDocs(@PathVariable String userId, HttpServletRequest httpServletRequest) {
    String jwtToken = getJwtTokenFromHeader(httpServletRequest);
    String callerUserId = tokenService.getUserId(jwtToken);

    return docService.fetchDocsForUserId(userId, callerUserId);
}

//    fetch a public document
@GetMapping("/{docId}")
@PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ANONYMOUS')")
public DocDTO fetchDocument(@PathVariable String docId, HttpServletRequest httpServletRequest) {

    String jwtToken = getJwtTokenFromHeader(httpServletRequest);
    String userId = tokenService.getUserId(jwtToken);

    return docService.fetchDoc(docId, userId);
}

//    fetch 10 most recent documents that are public
    @GetMapping("/recent")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ANONYMOUS')")
    public List<DocDTO> fetchRecentDocs() {

        return docService.fetchTopRecentDocs();
    }

//    modify his own documents
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public DocDTO updateDoc(@RequestBody DocDTO docDTO, HttpServletRequest httpServletRequest) throws UserNotAllowedException {

        String jwtToken = getJwtTokenFromHeader(httpServletRequest);
        String userId = tokenService.getUserId(jwtToken);

//        String tokenHeader = httpServletRequest.getHeader(AUTHORIZATION);
//        String jwtToken = StringUtils.removeStart(tokenHeader, "Bearer ").trim();
//        String userId = "fdgdfioioafdsfasdfadsf";
        docService.updateDoc(docDTO, userId);
        return docDTO;
    }

    private String getJwtTokenFromHeader(HttpServletRequest httpServletRequest) {

        try {
            String tokenHeader = httpServletRequest.getHeader(AUTHORIZATION);
            return StringUtils.removeStart(tokenHeader, "Bearer").trim();
        } catch (NullPointerException e) {
            return StringUtils.EMPTY;
        }
    }

//    delete his own documents
}

