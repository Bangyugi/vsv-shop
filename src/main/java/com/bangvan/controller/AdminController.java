package com.bangvan.controller;
import com.bangvan.dto.request.category.HomeCategoryRequest;
import com.bangvan.dto.response.ApiResponse;
import com.bangvan.service.HomeCategoryService;
import com.bangvan.service.SellerService;
import com.bangvan.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin", description = "Admin Management API")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final SellerService sellerService;
    private final HomeCategoryService homeCategoryService;

    @GetMapping("/users")
    @Operation(summary = "Get All Users", description = "Endpoint for admins to get a paginated list of all users.")
    public ResponseEntity<ApiResponse> getAllUsers(
            @RequestParam(value = "pageNo", defaultValue = "1", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "ASC", required = false) String sortDir
    ) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Users fetched successfully",
                userService.findAllUsers(pageable)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @GetMapping("/sellers")
    @Operation(summary = "Get All Sellers", description = "Endpoint for admins to get a paginated list of all sellers.")
    public ResponseEntity<ApiResponse> getAllSellers(
            @RequestParam(value= "pageNo", defaultValue = "1", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(value="sortDir", defaultValue = "ASC", required = false) String sortDir
    ) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Sellers fetched successfully",
                sellerService.getAllSellers(pageable)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @PostMapping("/home-categories")
    @Operation(summary = "Create a new Home Category", description = "Endpoint for admins to add a new category to the homepage layout.")
    public ResponseEntity<ApiResponse> createHomeCategory(@Valid @RequestBody HomeCategoryRequest request) {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.CREATED.value(),
                "Home category created successfully",
                homeCategoryService.createHomeCategory(request)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @GetMapping("/home-categories")
    @Operation(summary = "Get all Home Categories", description = "Endpoint for admins to retrieve all homepage categories.")
    public ResponseEntity<ApiResponse> getAllHomeCategories() {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Home categories fetched successfully",
                homeCategoryService.getAllHomeCategories()
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/home-categories/{id}")
    @Operation(summary = "Get Home Category by ID", description = "Endpoint to get a specific homepage category by its ID.")
    public ResponseEntity<ApiResponse> getHomeCategoryById(@PathVariable Long id) {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Home category found successfully",
                homeCategoryService.getHomeCategoryById(id)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping("/home-categories/{id}")
    @Operation(summary = "Update a Home Category", description = "Endpoint for admins to update an existing homepage category.")
    public ResponseEntity<ApiResponse> updateHomeCategory(@PathVariable Long id, @Valid @RequestBody HomeCategoryRequest request) {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Home category updated successfully",
                homeCategoryService.updateHomeCategory(id, request)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("/home-categories/{id}")
    @Operation(summary = "Delete a Home Category", description = "Endpoint for admins to delete a homepage category.")
    public ResponseEntity<ApiResponse> deleteHomeCategory(@PathVariable Long id) {
        homeCategoryService.deleteHomeCategory(id);
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Home category with ID " + id + " has been deleted successfully",
                null
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


}