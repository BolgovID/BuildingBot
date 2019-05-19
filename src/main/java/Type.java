public enum Type{
    ECONOM("Эконом"), KOMFORT("Комфорт"),
    BUSINESS("Бизнес"), PREMIUM("Премиум"),
    DELUX("Де-люкс");

    private String name;

    Type(String name){
        this.name=name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static boolean isExists(String str){
        int size=Type.values().length;
        for(int i = 0;i<size;i++){
            if(str.equals(Type.values()[i].name()))return true;
        }
        return false;
    }

    public static String getIdByName(String text){
        int size=Type.values().length;
        for(int i = 0;i<size;i++){
            if(Type.values()[i].name.equals(text)) return Type.values()[i].name();
        }
        System.out.println("Нет соответствий");
        return null;
    }

    public static String getNameById(String text){
        int size=Type.values().length;
        for(int i = 0;i<size;i++){
            if(Type.values()[i].name().equals(text)) return Type.values()[i].name;
        }
        return null;
    }
}
