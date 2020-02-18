package com.upgrad.FoodOrderingApp.api.controller;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.upgrad.FoodOrderingApp.api.model.ItemQuantity;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.*;
import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.upgrad.FoodOrderingApp.api.model.CouponDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.SaveOrderRequest;
import com.upgrad.FoodOrderingApp.api.model.SaveOrderResponse;
import com.upgrad.FoodOrderingApp.service.businness.OrderService;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;


@RestController
public class OrderController {
	@Autowired
	OrderService orderService;

	@Autowired
	ItemService itemService;

	@Autowired
	CustomerService customerService;

	@Autowired
	AddressService addressService;
	
	@CrossOrigin
	@RequestMapping(method=RequestMethod.GET,value="/order/coupon/{coupon_name}",produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<CouponDetailsResponse> getCouponByName(@PathVariable("coupon_name") String couponName) throws CouponNotFoundException, AuthorizationFailedException{
		CouponEntity couponEntity =  orderService.getCouponDetailByName(couponName);
		CouponDetailsResponse couponDetailsResponse= new CouponDetailsResponse();
		couponDetailsResponse.setCouponName(couponEntity.getCouponName());
		couponDetailsResponse.setId(UUID.fromString(couponEntity.getUuid()));
		couponDetailsResponse.setPercent(couponEntity.getPercent());
		return new ResponseEntity<CouponDetailsResponse>(couponDetailsResponse,HttpStatus.OK);
	}

	@CrossOrigin
	@GetMapping(value="/order",produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody ResponseEntity<OrderList> retrieveAllOrders(
			@RequestHeader("authorization") final String accessToken){

		String bearerToken = null;
		try {
			bearerToken = accessToken.split("Bearer ")[1];
		} catch (ArrayIndexOutOfBoundsException e) {
			bearerToken = accessToken;
		}

		CustomerEntity customer = customerService.getCustomerByToken(bearerToken);
		List<OrderEntity> orders = orderService.retrieveAllOrders(customer);

		OrderList orderList =  new OrderList();
        List<ItemQuantityResponse> items = new ArrayList<>();
		for(OrderEntity orderEntity: orders) {
			orderList.id(UUID.fromString(orderEntity.getUuid()));
			orderList.bill(orderEntity.getBill());

			/**
             * adding payment details
             * */

			orderList.payment(new OrderListPayment().
                    id(UUID.fromString(orderEntity.getPaymentEntity().getUuid())).
                    paymentName(orderEntity.getPaymentEntity().getPaymentName()));
			/**
             * adding coupon details
             * */
			orderList.coupon(new OrderListCoupon().
                    couponName(orderEntity.getCouponEntity().getCouponName()).
                    id(UUID.fromString(orderEntity.getCouponEntity().getUuid())).
                    percent(orderEntity.getCouponEntity().getPercent()));


            orderList.discount(orderEntity.getDiscount());
            orderList.date(String.valueOf(orderEntity.getDate()));

            /**
             * adding customer details
             * */
            orderList.customer(new OrderListCustomer().
                    id(UUID.fromString(customer.getUuid())).
            firstName(customer.getFirstName()).
                    lastName(customer.getLastName()).
                    contactNumber(customer.getContactNumber()).
                    emailAddress(customer.getEmail()));

            /**
             * adding address details
             * */
            orderList.address(new OrderListAddress().
                    id(UUID.fromString(orderEntity.getAddressEntity().getUuid())).
                    flatBuildingName(orderEntity.getAddressEntity().getFlat_buil_number()).
                    locality(orderEntity.getAddressEntity().getLocality()).
                    city(orderEntity.getAddressEntity().getCity()).
                    pincode(orderEntity.getAddressEntity().getPincode()).
                    state(new OrderListAddressState().
                            id(orderEntity.getAddressEntity().getStateEntity().getUuid()).
                            stateName(orderEntity.getAddressEntity().getStateEntity().getState_name())));

            /**
             * adding item details
             * */
            ItemQuantityResponse itemQuantityResponse = new ItemQuantityResponse();

            //fetch item details for every order from order item entity
            OrderItemEntity orderItemEntity = orderService.fetchItemDetails(orderEntity.getUuid());
            itemQuantityResponse.setPrice(orderItemEntity.getPrice());
            itemQuantityResponse.setQuantity(orderItemEntity.getQuantity());

            //fetch every order item detail from item entity
            itemQuantityResponse.setItem(new ItemQuantityResponseItem().
                    id(UUID.fromString(orderItemEntity.getItemEntity().getUuid())).
                    itemName(orderItemEntity.getItemEntity().getItem_name()).
                    itemPrice(orderItemEntity.getItemEntity().getPrice()).
                    type(ItemQuantityResponseItem.TypeEnum.fromValue(orderItemEntity.getItemEntity().getType())));


            items.add(itemQuantityResponse);
            orderList.itemQuantities(items);

		}
		return new ResponseEntity<OrderList>(orderList,HttpStatus.OK);
	}
	
	@CrossOrigin
	@RequestMapping(method=RequestMethod.POST,value="/order", consumes=MediaType.APPLICATION_JSON_UTF8_VALUE, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<SaveOrderResponse> postOrder(@RequestBody SaveOrderRequest saveOrderRequest, @RequestHeader("authorization") final String authorization) throws ItemNotFoundException, PaymentMethodNotFoundException, AuthorizationFailedException, RestaurantNotFoundException, CouponNotFoundException, AddressNotFoundException {
		String bearerToken = null;
		try {
			bearerToken = authorization.split("Bearer ")[1];
		} catch (ArrayIndexOutOfBoundsException e) {
			bearerToken = authorization;
		}
		OrderEntity orderEntity = new OrderEntity();
		String restaurantUuid = saveOrderRequest.getRestaurantId().toString();
		String couponUuid = saveOrderRequest.getCouponId().toString();
		String paymentUuid = saveOrderRequest.getPaymentId().toString();
		String addressUuid = saveOrderRequest.getAddressId();
		List<ItemQuantity> itemQuantities = saveOrderRequest.getItemQuantities();

		orderEntity.setBill(saveOrderRequest.getBill());
		orderEntity.setDiscount(saveOrderRequest.getDiscount());
		orderEntity.setDate(ZonedDateTime.now());
		orderEntity.setUuid(UUID.randomUUID().toString());

		OrderEntity orderEntity1 = orderService.postOrder(orderEntity, restaurantUuid, paymentUuid, couponUuid, addressUuid, bearerToken);

		for (int i=0; i < itemQuantities.size(); i++) {
			OrderItemEntity orderItemEntity = new OrderItemEntity();
			ItemEntity itemEntity = itemService.getItemById(itemQuantities.get(i).getItemId().toString());
			if(itemEntity == null) {
				throw new ItemNotFoundException("INF-003","No item by this id exist");
			}
			orderItemEntity.setPrice(itemQuantities.get(i).getPrice());
			orderItemEntity.setQuantity(itemQuantities.get(i).getQuantity());
			orderItemEntity.setItemEntity(itemEntity);
			orderItemEntity.setOrderEntity(orderEntity1);
			orderService.postOrderItem(orderItemEntity);
		}
		SaveOrderResponse saveOrderResponse = new SaveOrderResponse().id(orderEntity1.getUuid()).status("ORDER SUCCESSFULLY PLACED");
		return new ResponseEntity<SaveOrderResponse>(saveOrderResponse, HttpStatus.CREATED);
	}
}
