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

class FighterPlane extends AeroPlane {
    public void takeOff() {
        System.out.println("FighterPlane requires small size runway");
    }

    public void fly() {
        System.out.println("FighterPlane flies at high height");
    }
}

class Airport {
    public void poly(AeroPlane ref) {
        ref.takeOff();
        ref.fly();
        System.out.println("-------------------------------------");
    }
}

public class PolymorphismDemo {
    public static void main(String[] args) {
        CargoPlane cp = new CargoPlane();
        PassengerPlane pp = new PassengerPlane();
        FighterPlane fp = new FighterPlane();

        // Lesser code, pass the correct reference and invokechild class methods
        Airport a = new Airport();
        a.poly(cp);
        a.poly(pp);
        a.poly(fp);

        // cp.takeOff();
        // cp.fly();

        // pp.takeOff();
        // pp.fly();

        // fp.takeOff();
        // fp.fly();
    }
}
// Output
// CargoPlane requires longer runway
// Cargoplane flies at lower height
// -------------------------------------
// PassengerPlane requires medium size runway
// PassengerPlane flies at medium height
// -------------------------------------
// FighterPlane requires small size runway
// FighterPlane flies at high height
// -------------------------------------