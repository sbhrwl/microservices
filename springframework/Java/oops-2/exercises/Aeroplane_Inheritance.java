class AeroPlane {
    public void takeOff() {
        System.out.println("AeroPlane is taking off");
    }

    public void fly() {
        System.out.println("AeroPlane is Flying");
    }
}

class CargoPlane extends AeroPlane {
    public void fly() {
        System.out.println("Cargoplane flies at lower height");
    }

    public void carryGoods() {
        System.out.println("CargoPlane carries goods");
    }
}

class PassengerPlane extends AeroPlane {
    public void fly() {
        System.out.println("PassengerPlane flies at medium height");
    }

    public void carryPassenger() {
        System.out.println("PassengerPlane carries passenger");
    }

}

class Aeroplane_Inheritance {
    public static void main(String[] args) {
        CargoPlane cp = new CargoPlane();
        cp.takeOff();
        cp.fly();

        PassengerPlane pp = new PassengerPlane();
        pp.takeOff();
        pp.fly();
    }
}
// Output
// CargoPlane requires longer runway
// Cargoplane flies at lower height
// PassengerPlane requires medium size runway
// PassengerPlane flies at medium height