abstract class AeroPlane {
    abstract public void takeOff();

    abstract public void fly();

    public void landing() {
        System.out.println("Aeroplane is landing");
    }
}

class CargoPlane extends AeroPlane {
    public void takeOff() {
        System.out.println("CargoPlane requires longer runway");
    }

    public void fly() {
        System.out.println("Cargoplane flies at lower height");
    }

    public void alert() {
        System.out.println("Alert..");
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

public class AbstractionDemo {
    public static void main(String[] args) {
        AeroPlane ref_cp = new CargoPlane();
        ref_cp.takeOff();
        ref_cp.fly();
        ref_cp.landing();
        ((CargoPlane) ref_cp).alert();

        AeroPlane ref_pp = new PassengerPlane();
        ref_pp.takeOff();
        ref_pp.fly();
        ref_pp.landing();

        // AeroPlane ref=new AeroPlane();
    }
}
// Output
// CargoPlane requires longer runway
// Cargoplane flies at lower height
// Aeroplane is landing
// Alert..
// PassengerPlane requires medium size runway
// PassengerPlane flies at medium height
// Aeroplane is landing