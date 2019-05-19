public enum Space{
    MOSKOVSKI("пр-т Московский"),AROUND("Окружная"), SHEVCHENKO("ул. Шевченко"),
    SALTOVKA("Салтовка"), CENTR("Центр"),
    NAUKA("пр-т Науки"), KLOCHKI("ул. Клочковская"),
    BAVARIA("полт.Шлях"),SOKOL("Сокольники"), GAGARINA("пр-т Гагарина");

    private String name;

    Space(String name){
        this.name=name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static boolean isExists(String str){
        int size=Space.values().length;
        for(int i = 0;i<size;i++){
            if(str.equals(Space.values()[i].name()))return true;
        }
        return false;
    }

    public static String getIdByName(String text){
        int size=Space.values().length;
        for(int i = 0;i<size;i++){
            try {
                if(Space.values()[i].name.equals(text)) return Space.values()[i].name();
            }
           catch (NullPointerException e){
                continue;
           }
        }
        System.out.println("Нет соответствий");
        return null;
    }
}
