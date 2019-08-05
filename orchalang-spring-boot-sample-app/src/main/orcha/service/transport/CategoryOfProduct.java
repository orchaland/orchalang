package service.transport;

public class CategoryOfProduct {

    public enum Category{
        ELECTRICAL_APPLIANCE
    }

    public enum SubCategory{
        TV,
        SMART_PHONE
    }

    Category category;
    SubCategory subCategory;

}
