package com.musicboxd.server.controller;

import com.musicboxd.server.service.listenList.ListenListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/listen-list")
public class ListenListController {

    @Autowired
    ListenListService listenListService;

    @PostMapping("/add/{uri}")
    public ResponseEntity<String> addToListenList(@PathVariable String uri){
        boolean success = listenListService.addToListenList(uri);
        if (success) {
            return ResponseEntity.ok("Added to List successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to add into the List.");
        }
    }
    @DeleteMapping("remove/{uri}")
    public ResponseEntity<String> removeFromListenList(@PathVariable String uri) {
        boolean success = listenListService.removeFromListenList(uri);
        if (success) {
            return ResponseEntity.ok("Removed from the List successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to remove from the List.");
        }
    }
    @GetMapping("/getList")
    public ResponseEntity<?> getUserListenList(){
        return listenListService.getUserListenList();
    }
}
