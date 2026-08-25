package com.eudhari.model.shopkeppermodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductImageOption {
        private final String label;
        private final String url;

        public ProductImageOption(String label, String url) {
                this.label = label;
                this.url = url;
        }

        public String getLabel() {
                return label;
        }

        public String getUrl() {
                return url;
        }

        @Override
        public String toString() {
                return label;
        }

        private static final List<ProductImageOption> PRESETS = new ArrayList<>();

        static {
                PRESETS.add(new ProductImageOption("No Image (Default Placeholder)", ""));
                PRESETS.add(
                                new ProductImageOption(
                                                "Milk -> https://res.cloudinary.com/jvugluyf/image/upload/v1787078804/milk.jpg",
                                                "https://res.cloudinary.com/jvugluyf/image/upload/v1787078804/milk.jpg"));

                PRESETS.add(new ProductImageOption("Coconut oil",
                                "https://res.cloudinary.com/kbiqa2ky/image/upload/v1787480736/Coconut_oil.png"));

                PRESETS.add(new ProductImageOption("Rice ",
                                "https://res.cloudinary.com/kbiqa2ky/image/upload/v1787480774/Rice.png"));


                PRESETS.add(new ProductImageOption("Salt",
                                "https://res.cloudinary.com/kbiqa2ky/image/upload/v1787480748/Salt.png"));   
                                
                                
                PRESETS.add(new ProductImageOption("Fortune vegetable Oil",
                                "https://res.cloudinary.com/kbiqa2ky/image/upload/v1787480764/vegetable_Oil.png"));


                PRESETS.add(new ProductImageOption("Wheat Flour (Atta)",
                                "https://res.cloudinary.com/kbiqa2ky/image/upload/v1787483027/1iaXMjXUTikhh9xgRm7ecZ4B1OaxoJs4B_fd275451-ad86-4cfa-ba99-0f901f7b3773.png"));

                PRESETS.add(new ProductImageOption("Whole Wheat Bread",
                                "https://res.cloudinary.com/kbiqa2ky/image/upload/v1787481230/40092241_8-britannia-brown-bread-with-goodness-of-wheat-enriched-with-vitamins.png"));
                PRESETS.add(
                                new ProductImageOption("Tea",
                                                "https://res.cloudinary.com/kbiqa2ky/image/upload/v1787481393/Great-Value-Green-Tea-Bags-2-5-oz-40-Count_2b2b94e2-06d8-44c0-805b-c2167bb04066.64d802fc259fe698acadf34381cfedc1.jpg"));

                PRESETS.add(new ProductImageOption("Rasgulla",
                                "https://res.cloudinary.com/kbiqa2ky/image/upload/v1787481881/SWE042.jpg"));

                
                PRESETS.add(new ProductImageOption("Gulab jamun",
                                "https://res.cloudinary.com/kbiqa2ky/image/upload/v1787481863/71nbnGCL7lL.jpg"));
                                
                
                PRESETS.add(new ProductImageOption("Panner",
                                "https://res.cloudinary.com/kbiqa2ky/image/upload/v1787481842/81hD14MN91L.jpg"));

                
                PRESETS.add(new ProductImageOption("Basen",
                                "https://res.cloudinary.com/kbiqa2ky/image/upload/v1787481809/81JmTiQA68L.jpg"));


               
                PRESETS.add(new ProductImageOption("Dahi",
                                "https://res.cloudinary.com/kbiqa2ky/image/upload/v1787481962/Dahi-Thick-Fresh-1.png"));
                PRESETS.add(new ProductImageOption("Lotte choco pie",
                                "https://res.cloudinary.com/kbiqa2ky/image/upload/v1787482269/189909_10-lotte-choco-pie.png"));
                PRESETS.add(new ProductImageOption("Chana Dal",
                                "https://res.cloudinary.com/kbiqa2ky/image/upload/v1787481760/Haldiram_20Chana_20Dal_20-_2014_20OZ_20_28400_20GM_29.jpg"));
        }

        public static List<ProductImageOption> getPresets() {
                return Collections.unmodifiableList(PRESETS);
        }
}
