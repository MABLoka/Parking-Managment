package system;

import java.util.ArrayList;
import java.util.List;

public class LoginSubject {
    private List<LoginObserver> observers = new ArrayList<>();

    public void addObserver(LoginObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(LoginObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (LoginObserver observer : observers) {
            observer.onLoginSuccess();  // Notify all observers when login is successful
        }
    }

    public void login(String username, String password) {
        // Simulating login process
        boolean loginSuccessful = username.equals("client") && password.equals("password");  // Replace with actual logic

        if (loginSuccessful) {
            System.out.println("Login Successful!");
            notifyObservers();  // Notify observers on success
        } else {
            System.out.println("Login Failed!");
        }
    }
}

