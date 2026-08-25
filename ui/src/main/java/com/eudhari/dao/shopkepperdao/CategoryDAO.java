package com.eudhari.dao.shopkepperdao;

import com.eudhari.model.shopkeppermodel.CategoryModel;
import java.util.List;

public interface CategoryDAO {
    List<CategoryModel> getCategoriesByShopId(String shopId);

    void saveCategory(CategoryModel category);

    void deleteCategory(String shopId, String categoryName);
}
