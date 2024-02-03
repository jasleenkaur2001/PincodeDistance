package com.project.PinCode_Distance;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@RestController
class Controller {

    @PostMapping("/calculate-distance")
    public String calculateDistance(@RequestBody PincodeRequest pincodeRequest ){
        // Get latitude and longitude coordinates for the pincodes using a geocoding API
        String pincode1 = pincodeRequest.getPincode1();
        String pincode2 = pincodeRequest.getPincode2();
        DbManager dbManager = new DbManager();
        if(dbManager.checkExist(pincode1 , pincode2)){
            return "Distance between " + pincode1 + " and " + pincode2 + " is " + dbManager.getDistance(pincode1 , pincode2) + " kilometers.";
        }
        locationManager locationManager = new locationManager();
        double[] coordinates1 = locationManager.getCoordinates(pincode1);
        double[] coordinates2 = locationManager.getCoordinates(pincode2);
        double distance = locationManager.calculateDistance(coordinates1[0], coordinates1[1], coordinates2[0], coordinates2[1]);
        dbManager.addDistance(pincode1 , pincode2 , distance);
        dbManager.saveToFile();
        return "Distance between " + pincode1 + " and " + pincode2 + " is " + distance + " kilometers.";
    }

}