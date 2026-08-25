package com.eudhari.dao.shopkepperdao;

import com.eudhari.model.shopkeppermodel.ProductModel;
import java.util.List;

public interface ProductDAO {
    List<ProductModel> getAllProducts();

    ProductModel getProductById(String id);

    List<ProductModel> getProductsByShopId(String shopId);

    void saveProduct(ProductModel product);

    void updateProduct(ProductModel product);

    void deleteProduct(String id);

    void restoreProduct(String id);
}
