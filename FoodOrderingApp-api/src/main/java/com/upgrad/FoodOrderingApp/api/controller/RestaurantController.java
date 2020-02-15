package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import io.swagger.models.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private CategoryService categoryService;

    @RequestMapping(method = RequestMethod.GET, path = "/restaurant",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayList>getAllRestaurants() {
        final List<RestaurantEntity> restaurantEntities = restaurantService.getAllRestaurants();
        List<RestaurantDetailsResponse> restaurantResponseDetails = new ArrayList<RestaurantDetailsResponse>();

        for(int i=0; i < restaurantEntities.size(); i++) {
            List<CategoryEntity> categoryEntities = restaurantEntities.get(i).getCategories();
            List<CategoryList> categoriesList = new ArrayList<CategoryList>();

            for(int j = 0; j< categoryEntities.size(); j++){
                CategoryList catList = new CategoryList();
                catList.setCategoryName(categoryEntities.get(j).getCategory_name());
                catList.setId(UUID.fromString(categoryEntities.get(j).getUuid()));

                List<ItemEntity> itemEntities = categoryEntities.get(j).getItem();
                List<ItemList> itemLists = new ArrayList<ItemList>();
                for(int k = 0; k < itemEntities.size(); k++) {
                    ItemList itemList = new ItemList();
                    itemList.setId(UUID.fromString(itemEntities.get(k).getUuid()));
                    itemList.setItemName(itemEntities.get(k).getItem_name());
                    ItemList.ItemTypeEnum itemType = ItemList.ItemTypeEnum.values()[Integer.parseInt(itemEntities.get(k).getType())];
                    itemList.setItemType(itemType);
                    itemList.setPrice(itemEntities.get(k).getPrice());
                    itemLists.add(itemList);
                }
                catList.setItemList(itemLists);
                categoriesList.add(catList);
            }
            restaurantResponseDetails.add(
                    new RestaurantDetailsResponse()
                            .id(UUID.fromString(restaurantEntities.get(i).getUuid()))
                            .restaurantName(restaurantEntities.get(i).getRestaurant_name())
                            .photoURL(restaurantEntities.get(i).getPhoto_url())
                            .customerRating(restaurantEntities.get(i).getCustomer_rating())
                            .averagePrice(restaurantEntities.get(i).getAverage_price_for_two())
                            .numberCustomersRated(restaurantEntities.get(i).getNumber_of_customers_rated())
                            .id(UUID.fromString(restaurantEntities.get(i).getUuid()))
                            .restaurantName(restaurantEntities.get(i).getRestaurant_name())
                            .address(new RestaurantDetailsResponseAddress()
                                    .id(UUID.fromString(restaurantEntities.get(i).getAddressEntity().getUuid()))
                                    .flatBuildingName(restaurantEntities.get(i).getAddressEntity().getFlat_buil_number())
                                    .locality(restaurantEntities.get(i).getAddressEntity().getLocality())
                                    .city(restaurantEntities.get(i).getAddressEntity().getCity())
                                    .pincode(restaurantEntities.get(i).getAddressEntity().getPincode())
                                    .state(new RestaurantDetailsResponseAddressState().id(restaurantEntities.get(i).getAddressEntity().getStateEntity().getUuid())
                                            .stateName(restaurantEntities.get(i).getAddressEntity().getStateEntity().getState_name())
                                    )
                            )
                            .categories(categoriesList)
            );
        }
        return new ResponseEntity<ArrayList>((ArrayList)restaurantResponseDetails, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/name/{restaurant_name}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayList> getRestaurantsByName(@PathVariable("restaurant_name") final String restaurantName)throws RestaurantNotFoundException {
        String lowerrestaurantName = restaurantName.toLowerCase();
        final List<RestaurantEntity> restbyNameEntitiesList = restaurantService.getRestaurantsByName(lowerrestaurantName);
        List<RestaurantDetailsResponse> restbyNameDetailsResponseList = new ArrayList<RestaurantDetailsResponse>();
        for(int i=0; i < restbyNameEntitiesList.size(); i++) {
            List<CategoryEntity> categoryEntities = restbyNameEntitiesList.get(i).getCategories();
            List<CategoryList> categoriesList = new ArrayList<CategoryList>();

            for(int j = 0; j< categoryEntities.size(); j++){
                CategoryList catList = new CategoryList();
                catList.setCategoryName(categoryEntities.get(j).getCategory_name());
                catList.setId(UUID.fromString(categoryEntities.get(j).getUuid()));
                List<ItemEntity> itemEntities = categoryEntities.get(j).getItem();
                List<ItemList> itemLists = new ArrayList<ItemList>();
                for(int k = 0; k < itemEntities.size(); k++) {
                    ItemList itemList = new ItemList();
                    itemList.setId(UUID.fromString(itemEntities.get(k).getUuid()));
                    itemList.setItemName(itemEntities.get(k).getItem_name());
                    ItemList.ItemTypeEnum itemType = ItemList.ItemTypeEnum.values()[Integer.parseInt(itemEntities.get(k).getType())];
                    itemList.setItemType(itemType);
                    itemList.setPrice(itemEntities.get(k).getPrice());
                    itemLists.add(itemList);
                }
                catList.setItemList(itemLists);
                categoriesList.add(catList);
            }
            restbyNameDetailsResponseList.add(
                    new RestaurantDetailsResponse()
                            .id(UUID.fromString(restbyNameEntitiesList.get(i).getUuid()))
                            .restaurantName(restbyNameEntitiesList.get(i).getRestaurant_name())
                            .photoURL(restbyNameEntitiesList.get(i).getPhoto_url())
                            .customerRating(restbyNameEntitiesList.get(i).getCustomer_rating())
                            .averagePrice(restbyNameEntitiesList.get(i).getAverage_price_for_two())
                            .numberCustomersRated(restbyNameEntitiesList.get(i).getNumber_of_customers_rated())
                            .address(new RestaurantDetailsResponseAddress()
                                    .id(UUID.fromString(restbyNameEntitiesList.get(i).getAddressEntity().getUuid()))
                                    .flatBuildingName(restbyNameEntitiesList.get(i).getAddressEntity().getFlat_buil_number())
                                    .locality(restbyNameEntitiesList.get(i).getAddressEntity().getLocality())
                                    .city(restbyNameEntitiesList.get(i).getAddressEntity().getCity())
                                    .pincode(restbyNameEntitiesList.get(i).getAddressEntity().getPincode())
                                    .state(new RestaurantDetailsResponseAddressState().id(restbyNameEntitiesList.get(i).getAddressEntity().getStateEntity().getUuid())
                                            .stateName(restbyNameEntitiesList.get(i).getAddressEntity().getStateEntity().getState_name())
                                    )
                            )
                            .categories(categoriesList)
            );
        }
        return new ResponseEntity<ArrayList>((ArrayList)restbyNameDetailsResponseList, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/category/{category_id}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayList> getRestaurantsByCategoryId(@PathVariable final String category_id) throws CategoryNotFoundException {
        CategoryEntity categoryEntity = categoryService.getCategoryById(category_id);
        List<RestaurantEntity> restaurantEntities = categoryEntity.getRestaurant();
        List<RestaurantDetailsResponse> restaurantDetailsResponses = new ArrayList<RestaurantDetailsResponse>();
        for(int i=0; i < restaurantEntities.size(); i++) {
            List<CategoryEntity> categoryEntities = restaurantEntities.get(i).getCategories();
            List<CategoryList> categoriesList = new ArrayList<CategoryList>();
            for (int j=0; j < categoryEntities.size(); j++) {
                CategoryList catList = new CategoryList();
                catList.setCategoryName(categoryEntities.get(j).getCategory_name());
                catList.setId(UUID.fromString(categoryEntities.get(j).getUuid()));

                List<ItemEntity> itemEntities = categoryEntities.get(j).getItem();
                List<ItemList> itemLists = new ArrayList<ItemList>();
                for(int k = 0; k < itemEntities.size(); k++) {
                    ItemList itemList = new ItemList();
                    itemList.setId(UUID.fromString(itemEntities.get(k).getUuid()));
                    itemList.setItemName(itemEntities.get(k).getItem_name());
                    ItemList.ItemTypeEnum itemType = ItemList.ItemTypeEnum.values()[Integer.parseInt(itemEntities.get(k).getType())];
                    itemList.setItemType(itemType);
                    itemList.setPrice(itemEntities.get(k).getPrice());
                    itemLists.add(itemList);
                }
                catList.setItemList(itemLists);
                categoriesList.add(catList);
            }
            restaurantDetailsResponses.add(
                    new RestaurantDetailsResponse()
                            .id(UUID.fromString(restaurantEntities.get(i).getUuid()))
                            .restaurantName(restaurantEntities.get(i).getRestaurant_name())
                            .photoURL(restaurantEntities.get(i).getPhoto_url())
                            .customerRating(restaurantEntities.get(i).getCustomer_rating())
                            .averagePrice(restaurantEntities.get(i).getAverage_price_for_two())
                            .numberCustomersRated(restaurantEntities.get(i).getNumber_of_customers_rated())
                            .id(UUID.fromString(restaurantEntities.get(i).getUuid()))
                            .restaurantName(restaurantEntities.get(i).getRestaurant_name())
                            .address(new RestaurantDetailsResponseAddress()
                                    .id(UUID.fromString(restaurantEntities.get(i).getAddressEntity().getUuid()))
                                    .flatBuildingName(restaurantEntities.get(i).getAddressEntity().getFlat_buil_number())
                                    .locality(restaurantEntities.get(i).getAddressEntity().getLocality())
                                    .city(restaurantEntities.get(i).getAddressEntity().getCity())
                                    .pincode(restaurantEntities.get(i).getAddressEntity().getPincode())
                                    .state(new RestaurantDetailsResponseAddressState().id(restaurantEntities.get(i).getAddressEntity().getStateEntity().getUuid())
                                            .stateName(restaurantEntities.get(i).getAddressEntity().getStateEntity().getState_name())
                                    )
                            )
                            .categories(categoriesList)
            );
        }
        return new ResponseEntity<ArrayList>((ArrayList)restaurantDetailsResponses, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/{restaurant_id}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getRestaurantById(@PathVariable final String restaurant_id) throws RestaurantNotFoundException {
        RestaurantEntity restaurantEntity = restaurantService.getRestaurantById(restaurant_id);

        List<CategoryEntity> categoryEntities = restaurantEntity.getCategories();
        List<CategoryList> categoriesList = new ArrayList<CategoryList>();
        for (int j=0; j < categoryEntities.size(); j++) {
            CategoryList catList = new CategoryList();
            catList.setCategoryName(categoryEntities.get(j).getCategory_name());
            catList.setId(UUID.fromString(categoryEntities.get(j).getUuid()));

            List<ItemEntity> itemEntities = categoryEntities.get(j).getItem();
            List<ItemList> itemLists = new ArrayList<ItemList>();
            for (int k = 0; k < itemEntities.size(); k++) {
                ItemList itemList = new ItemList();
                itemList.setId(UUID.fromString(itemEntities.get(k).getUuid()));
                itemList.setItemName(itemEntities.get(k).getItem_name());
                ItemList.ItemTypeEnum itemType = ItemList.ItemTypeEnum.values()[Integer.parseInt(itemEntities.get(k).getType())];
                itemList.setItemType(itemType);
                itemList.setPrice(itemEntities.get(k).getPrice());
                itemLists.add(itemList);
            }
            catList.setItemList(itemLists);
            categoriesList.add(catList);
        }
        RestaurantDetailsResponse restResponse = new RestaurantDetailsResponse()
                .id(UUID.fromString(restaurantEntity.getUuid()))
                .restaurantName(restaurantEntity.getRestaurant_name())
                .photoURL(restaurantEntity.getPhoto_url())
                .customerRating(restaurantEntity.getCustomer_rating())
                .averagePrice(restaurantEntity.getAverage_price_for_two())
                .numberCustomersRated(restaurantEntity.getNumber_of_customers_rated())
                .address(new RestaurantDetailsResponseAddress()
                        .id(UUID.fromString(restaurantEntity.getAddressEntity().getUuid()))
                        .flatBuildingName(restaurantEntity.getAddressEntity().getFlat_buil_number())
                        .locality(restaurantEntity.getAddressEntity().getLocality())
                        .city(restaurantEntity.getAddressEntity().getCity())
                        .pincode(restaurantEntity.getAddressEntity().getPincode())
                        .state(new RestaurantDetailsResponseAddressState().id(restaurantEntity.getAddressEntity().getStateEntity().getUuid())
                                .stateName(restaurantEntity.getAddressEntity().getStateEntity().getState_name())
                        )
                )
                .categories(categoriesList);
        return new ResponseEntity<RestaurantDetailsResponse>(restResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/restaurant/{restaurant_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantUpdatedResponse> updateRestaurantDetails(@RequestParam Double customerRating , @PathVariable("restaurant_id") final String restaurant_id) throws RestaurantNotFoundException, InvalidRatingException {
        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setUuid(restaurant_id);
        restaurantEntity.setCustomer_rating(BigDecimal.valueOf(customerRating));
        RestaurantEntity updatedRestaurantEntity = restaurantService.updateRestaurantDetails(restaurantEntity);
        RestaurantUpdatedResponse restUpdateResponse = new RestaurantUpdatedResponse()
                .id(UUID.fromString(updatedRestaurantEntity.getUuid()))
                .status("RESTAURANT RATING UPDATED SUCCESSFULLY");
        return new ResponseEntity<RestaurantUpdatedResponse>(restUpdateResponse, HttpStatus.OK);
    }
}
