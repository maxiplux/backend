package app.quantun.backend.controller;

import app.quantun.backend.exception.CategoryNotFoundException;
import app.quantun.backend.models.contract.request.CategoryRequestDTO;
import app.quantun.backend.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "categories/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new CategoryRequestDTO());
        return "categories/form";
    }

    @PostMapping
    public String createCategory(@Valid @ModelAttribute("category") CategoryRequestDTO category,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "categories/form";
        }
        categoryService.createCategory(category);
        redirectAttributes.addFlashAttribute("message", "Category created successfully");
        return "redirect:/categories";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        var category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        model.addAttribute("category", category);
        return "categories/form";
    }

    @PostMapping("/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @Valid @ModelAttribute("category") CategoryRequestDTO category,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "categories/form";
        }
        categoryService.updateCategory(id, category);
        redirectAttributes.addFlashAttribute("message", "Category updated successfully");
        return "redirect:/categories";
    }

    @DeleteMapping("/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoryService.deleteCategory(id);
        redirectAttributes.addFlashAttribute("message", "Category deleted successfully");
        return "redirect:/categories";
    }

    @GetMapping("/search")
    public String searchCategories(@RequestParam String name, Model model) {
        model.addAttribute("categories", categoryService.searchCategoriesByName(name));
        return "categories/list";
    }

    @GetMapping("/{id}/products")
    public String getCategoryProducts(@PathVariable Long id, Model model) {
        var category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        model.addAttribute("category", category);
        model.addAttribute("products", categoryService.getProductsByCategory(id));
        return "categories/products";
    }
}
