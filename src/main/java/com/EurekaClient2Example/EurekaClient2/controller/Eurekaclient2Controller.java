package com.EurekaClient2Example.EurekaClient2.controller;

import com.EurekaClient2Example.EurekaClient2.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping("/consumer")
public class Eurekaclient2Controller {
    @Autowired
    Environment environment;
    private static List productList = new ArrayList<>();

    @Autowired
    private LoadBalancerClient loadBalancerClient;
    private RestTemplate restTemplate = new RestTemplate();

    static{
        productList = new ArrayList<>();
        Product p1 = new Product(UUID.randomUUID(), "Guitar" , 99.99, "Music");
        Product p2 = new Product(UUID.randomUUID(), "AWS Book" , 29.99, "Books");
        Product p3 = new Product(UUID.randomUUID(), "Bread" , 9.99, "Food");
        Product p4 = new Product(UUID.randomUUID(), "Soap" , 99.99, "Body");
        Product p5 = new Product(UUID.randomUUID(), "Milk" , 3.99, "Food");
        Product p6 = new Product(UUID.randomUUID(), "Pant" , 17.99, " Cloth");
        Product p7 = new Product(UUID.randomUUID(), "Tyre" , 39.99, "Auto");
        productList.add(p1);
        productList.add(p2);
        productList.add(p3);
        productList.add(p4);
        productList.add(p5);
        productList.add(p6);
        productList.add(p7);
    }

    @GetMapping("/calleurekaclient2")
    public ResponseEntity callEurekaClient2(){
        return new ResponseEntity("Hello From Client 2 From port No. "+environment.getProperty("local.server.port"), HttpStatus.OK);
    }

    @GetMapping(value = "/allproducts", produces = MediaType.APPLICATION_JSON_VALUE)
    public List list() {
        return Eurekaclient2Controller.productList;
    }

    @GetMapping(value = "/product/{id}" , produces = MediaType.APPLICATION_JSON_VALUE)
    public Product showProduct(@PathVariable UUID id) {
        return new Product(id, "Guitar" , 99.99, "Music");
    }

    @PostMapping(value = "/product/update")
    public String saveProduct(@RequestBody Product product) {
        return "Product with product id: "+ product.getProductId() +" and product name:"+product.getProductName()+" has been saved successfully!";
    }

    @DeleteMapping(value = "/product/delete/{id}")
    public void delete(@PathVariable UUID id) {
        //log "Product "+ id +" has been deleted successfully!";
    }
    @GetMapping("/callEurekaClient1viaClient2")
    public ResponseEntity callClient2(){
        try {
            return new ResponseEntity(
                    restTemplate.getForObject(getEurkaClient1BaseUri() + "/employee/calleurekaclient1", String.class), HttpStatus.OK);
        }catch (Exception exp) {
            return new ResponseEntity(
                    restTemplate.getForObject(getEurkaClient1BaseUri() + "/employee/calleurekaclient1", String.class), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    private String getEurkaClient1BaseUri(){
        ServiceInstance serviceInstance =  loadBalancerClient.choose("EUREKA-CLIENT1");
        return serviceInstance.getUri().toString();
    }
}