package com.res.shein;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController
{
	
	 private final ProductService productService;

	    public UserController(ProductService productService) {
	        this.productService = productService;
	    }

	    @GetMapping("/home")
	    public String home(@RequestParam(defaultValue = "0") int page,
	                       @RequestParam(defaultValue = "6") int size,
	                       Model model) {
	        Page<Product> productPage = productService.getAllProducts(page, size);
	        model.addAttribute("productPage", productPage);
	        model.addAttribute("currentPage", page);
	        model.addAttribute("totalPages", productPage.getTotalPages());
	        return "user/home";
	    }
	    @GetMapping("/search")
	    public String search(
	            @RequestParam(value = "category", required = false) String category,
	            @RequestParam(value = "keyword", required = false) String keyword,
	            @RequestParam(defaultValue = "0") int page,
	            @RequestParam(defaultValue = "6") int size,
	            Model model) {
	        
	    	Page<Product> productPage;

	        if (keyword != null && !keyword.isEmpty()) {
	            // Perform a search based on keyword
	            productPage = productService.searchProducts(keyword, page, size);
	            if (productPage.isEmpty()) {
	                model.addAttribute("message", "No products found for keyword: " + keyword);
	            }
	        } else if (category != null && !category.isEmpty()) {
	            // Filter by category if no keyword is provided
	            productPage = productService.getProductsByCategory(category, page, size);
	        } else {
	            // Fetch all products if no filters are applied
	            productPage = productService.getAllProducts(page, size);
	        }

	        model.addAttribute("productPage", productPage);
	        model.addAttribute("currentPage", page);
	        model.addAttribute("totalPages", productPage.getTotalPages());
	        model.addAttribute("keyword", keyword);
	        model.addAttribute("category", category);

	        // Add a message for empty results if not already set
	        if (productPage.isEmpty() && (keyword != null && !keyword.isEmpty())) {
	            model.addAttribute("message", "No products found for keyword: " + keyword);
	        }

	        return "user/products"; // Ensure 'user/products.html' is correctly set up to handle and display the results
	    }

	    
	    @GetMapping("/products")
	    public String getProductsByCategory(
	        @RequestParam("category") String category,
	        @RequestParam(value = "page", defaultValue = "0") int page,
	        @RequestParam(value = "size", defaultValue = "6") int size,
	        Model model) {

	        Page<Product> productPage = productService.getProductsByCategory(category, page, size);
	        model.addAttribute("productPage", productPage);
	        model.addAttribute("category", category); // Add category to the model for pagination links
	        return "user/products";
	    }

}
