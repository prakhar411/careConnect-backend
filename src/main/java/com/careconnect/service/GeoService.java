package com.careconnect.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GeoService {

    private static final List<String> STATES;
    private static final Map<String, List<String>> CITIES;

    static {
        STATES = List.of(
            "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh",
            "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand",
            "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur",
            "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab",
            "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura",
            "Uttar Pradesh", "Uttarakhand", "West Bengal",
            "Andaman and Nicobar Islands", "Chandigarh", "Dadra and Nagar Haveli",
            "Daman and Diu", "Delhi", "Jammu and Kashmir", "Ladakh",
            "Lakshadweep", "Puducherry"
        );

        Map<String, List<String>> c = new LinkedHashMap<>();
        c.put("Andhra Pradesh",     List.of("Visakhapatnam","Vijayawada","Guntur","Nellore","Kurnool","Tirupati","Rajahmundry","Kadapa","Anantapur","Eluru"));
        c.put("Arunachal Pradesh",  List.of("Itanagar","Naharlagun","Tawang","Ziro","Pasighat","Bomdila","Tezu"));
        c.put("Assam",              List.of("Guwahati","Dibrugarh","Jorhat","Silchar","Tezpur","Nagaon","Tinsukia","Bongaigaon","Karimganj"));
        c.put("Bihar",              List.of("Patna","Gaya","Bhagalpur","Muzaffarpur","Purnia","Darbhanga","Bihar Sharif","Arrah","Begusarai","Katihar"));
        c.put("Chhattisgarh",       List.of("Raipur","Bhilai","Bilaspur","Korba","Durg","Rajnandgaon","Jagdalpur","Ambikapur","Raigarh"));
        c.put("Goa",                List.of("Panaji","Vasco da Gama","Margao","Mapusa","Ponda","Bicholim","Mormugao"));
        c.put("Gujarat",            List.of("Ahmedabad","Surat","Vadodara","Rajkot","Bhavnagar","Jamnagar","Gandhinagar","Junagadh","Anand","Nadiad","Morbi"));
        c.put("Haryana",            List.of("Faridabad","Gurugram","Panipat","Ambala","Yamunanagar","Rohtak","Hisar","Karnal","Sonipat","Panchkula","Rewari"));
        c.put("Himachal Pradesh",   List.of("Shimla","Dharamshala","Solan","Mandi","Kullu","Hamirpur","Baddi","Nahan","Palampur"));
        c.put("Jharkhand",          List.of("Ranchi","Jamshedpur","Dhanbad","Bokaro","Hazaribag","Deoghar","Giridih","Ramgarh","Phusro"));
        c.put("Karnataka",          List.of("Bengaluru","Mysuru","Hubli","Mangaluru","Belagavi","Kalaburagi","Ballari","Tumkur","Davangere","Shivamogga","Bidar","Udupi"));
        c.put("Kerala",             List.of("Thiruvananthapuram","Kochi","Kozhikode","Thrissur","Kannur","Kollam","Alappuzha","Palakkad","Malappuram","Kottayam","Idukki"));
        c.put("Madhya Pradesh",     List.of("Bhopal","Indore","Jabalpur","Gwalior","Ujjain","Sagar","Dewas","Satna","Rewa","Ratlam","Murwara","Burhanpur"));
        c.put("Maharashtra",        List.of("Mumbai","Pune","Nagpur","Nashik","Aurangabad","Solapur","Thane","Nanded","Kolhapur","Amravati","Akola","Latur","Dhule","Ahmednagar"));
        c.put("Manipur",            List.of("Imphal","Thoubal","Bishnupur","Churachandpur","Senapati"));
        c.put("Meghalaya",          List.of("Shillong","Tura","Jowai","Nongstoin","Baghmara"));
        c.put("Mizoram",            List.of("Aizawl","Lunglei","Saiha","Champhai","Kolasib"));
        c.put("Nagaland",           List.of("Kohima","Dimapur","Mokokchung","Wokha","Tuensang","Zunheboto"));
        c.put("Odisha",             List.of("Bhubaneswar","Cuttack","Rourkela","Berhampur","Sambalpur","Puri","Balasore","Baripada","Bhadrak","Jharsuguda"));
        c.put("Punjab",             List.of("Ludhiana","Amritsar","Jalandhar","Patiala","Bathinda","Mohali","Firozpur","Gurdaspur","Hoshiarpur","Pathankot","Moga"));
        c.put("Rajasthan",          List.of("Jaipur","Jodhpur","Udaipur","Kota","Bikaner","Ajmer","Alwar","Bharatpur","Sikar","Pali","Sri Ganganagar","Bhilwara"));
        c.put("Sikkim",             List.of("Gangtok","Namchi","Gyalshing","Mangan","Rangpo"));
        c.put("Tamil Nadu",         List.of("Chennai","Coimbatore","Madurai","Tiruchirappalli","Salem","Tirunelveli","Vellore","Erode","Tiruppur","Thoothukudi","Dindigul","Thanjavur","Kanchipuram"));
        c.put("Telangana",          List.of("Hyderabad","Warangal","Nizamabad","Karimnagar","Khammam","Ramagundam","Mahbubnagar","Nalgonda","Adilabad","Suryapet"));
        c.put("Tripura",            List.of("Agartala","Udaipur","Dharmanagar","Kailasahar","Belonia","Ambassa"));
        c.put("Uttar Pradesh",      List.of("Lucknow","Kanpur","Agra","Varanasi","Meerut","Prayagraj","Ghaziabad","Noida","Bareilly","Aligarh","Moradabad","Gorakhpur","Firozabad","Saharanpur","Mathura","Jhansi","Muzaffarnagar"));
        c.put("Uttarakhand",        List.of("Dehradun","Haridwar","Rishikesh","Roorkee","Haldwani","Nainital","Mussoorie","Kashipur","Rudrapur","Kotdwara"));
        c.put("West Bengal",        List.of("Kolkata","Asansol","Siliguri","Durgapur","Bardhaman","Malda","Howrah","Kharagpur","Haldia","Raiganj","Cooch Behar","Medinipur"));
        c.put("Andaman and Nicobar Islands", List.of("Port Blair","Havelock Island","Car Nicobar","Diglipur","Little Andaman"));
        c.put("Chandigarh",         List.of("Chandigarh"));
        c.put("Dadra and Nagar Haveli", List.of("Silvassa","Amli","Dadra"));
        c.put("Daman and Diu",      List.of("Daman","Diu","Nani Daman"));
        c.put("Delhi",              List.of("New Delhi","Dwarka","Rohini","Janakpuri","Karol Bagh","Lajpat Nagar","Saket","Pitampura","Connaught Place","Noida Extension","Preet Vihar","Nehru Place"));
        c.put("Jammu and Kashmir",  List.of("Srinagar","Jammu","Anantnag","Sopore","Baramulla","Udhampur","Kathua","Poonch","Rajouri"));
        c.put("Ladakh",             List.of("Leh","Kargil","Diskit","Nubra"));
        c.put("Lakshadweep",        List.of("Kavaratti","Agatti","Amini","Andrott"));
        c.put("Puducherry",         List.of("Puducherry","Karaikal","Mahe","Yanam","Oulgaret"));
        CITIES = Collections.unmodifiableMap(c);
    }

    public List<String> getStates() {
        return STATES;
    }

    public List<String> getCities(String state) {
        return CITIES.getOrDefault(state, List.of());
    }
}
