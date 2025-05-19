class AeroPlane {
    public void takeOff() {
        System.out.println("AeroPlane is taking off");
    }

    public void fly() {
        System.out.println("AeroPlane is Flying");
    }
}

class CargoPlane extends AeroPlane {
    public void takeOff() {
        System.out.println("CargoPlane requires longer runway");
    }

    public void fly() {
        System.out.println("Cargoplane flies at lower height");
    }
}

class PassengerPlane extends AeroPlane {
    public void takeOff() {
        System.out.println("PassengerPlane requires medium size runway");
    }

    public void fly() {
        System.out.println("PassengerPlane flies at medium height");
    }
}

public class CallChildClassMethodWithParentClassRef {
    public static void main(String[] args) {
        CargoPlane cp = new CargoPlane();
        PassengerPlane pp = new PassengerPlane();
        AeroPlane ref;

        // Assign child class ref to parent class ref
        ref = cp;

        // Use parent class ref to invoke child class methods
        ref.takeOff();
        ref.fly();

        System.out.println("------------------------------------------------");
        ref = pp;

        ref.takeOff();
        ref.fly();
        // pp=cp;
    }
}
// Output
// CargoPlane requires longer runway
// Cargoplane flies at lower height
// ------------------------------------------------
// PassengerPlane requires medium size runway
// PassengerPlane flies at medium height