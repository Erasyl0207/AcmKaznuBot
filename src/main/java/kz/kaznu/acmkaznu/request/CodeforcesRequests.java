package kz.kaznu.acmkaznu.request;

import kz.kaznu.acmkaznu.model.UserCodeforces;

import java.util.HashMap;

public interface CodeforcesRequests {

    String usersIsValid(String users);
    UserCodeforces getUserInfo(String handle);

    HashMap<String, Integer> getUserSubmissions(String handle);
}
