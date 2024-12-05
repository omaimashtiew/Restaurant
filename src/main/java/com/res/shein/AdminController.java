package com.res.shein;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/admin")
public class AdminController {
	
	private final ProductService productService;

    public AdminController(ProductService productService) {
        this.productService = productService;
    }
    
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        return "admin/add-product";
    }
    
    @PostMapping("/add")
    public String addProduct(@ModelAttribute Product product, @RequestParam("photo") MultipartFile photo) {
        try {
            String fileName = photo.getOriginalFilename();
            Path path = Paths.get("src/main/resources/static/images/" + fileName);
            Files.write(path, photo.getBytes());
            
            product.setPhotoUrl(fileName);
            System.out.println("Adding product with category: " + product.getCategory());

            productService.saveProduct(product);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/admin/products";
    }

    
    @GetMapping("/products")
    public String getProducts(Model model,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "6") int size) {
        Page<Product> productPage = productService.getAllProducts(page, size);
        model.addAttribute("productPage", productPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        return "admin/product-list";
    }
    
    @GetMapping("/productsadmin")
    public String getProductsByCategory(
        @RequestParam("category") String category,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "6") int size,
        Model model) {
        Page<Product> productPage = productService.getProductsByCategory(category, page, size);
        model.addAttribute("productPage", productPage);
        model.addAttribute("category", category); 
        return "admin/productsadmin";
    }

    
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/admin/products";
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

        if (productPage.isEmpty() && (keyword != null && !keyword.isEmpty())) {
            model.addAttribute("message", "No products found for keyword: " + keyword);
        }

        return "admin/productsadmin"; // Ensure 'user/products.html' is correctly set up to handle and display the results
    }
    
    
    @PostMapping("/update")
    public String updateProduct(@ModelAttribute Product product) {

        productService.saveProduct(product);

        return "redirect:/admin/products";
    }

    
    

}

