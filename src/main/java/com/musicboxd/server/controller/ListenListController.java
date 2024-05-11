package com.musicboxd.server.controller;

import com.musicboxd.server.service.listenList.ListenListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/listen-list")
public class ListenListController {

    @Autowired
    ListenListService listenListService;

    @PostMapping("/add/{albumId}")
    public ResponseEntity<String> addToListenList(@PathVariable String albumId){
        boolean success = listenListService.addToListenList(albumId);
        if (success) {
            return ResponseEntity.ok("Added to List successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to add into the List.");
        }
    }
    @DeleteMapping("remove/{albumId}")
    public ResponseEntity<String> removeFromListenList(@PathVariable String albumId) {
        boolean success = listenListService.removeFromListenList(albumId);
        if (success) {
            return ResponseEntity.ok("Removed from the List successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to remove from the List.");
        }
    }
}
