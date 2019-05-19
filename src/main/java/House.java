import java.io.IOException;
import java.util.ArrayList;

class House {

    private String name;
    private String type;
    private String space;
    private String ref;

     private static ArrayList<House> houses = new ArrayList<>();

    private House(String name, String type, String space, String ref) {
        this.name = name;
        this.type = type;
        this.space = space;
        this.ref = ref;
        houses.add(this);
    }

    public static void LoadHouse() throws IOException {
        ExcelParser.readFromExcel();
        System.out.println("База данных домов обновленна");
    }

    public static void CreateHouse(String value, String type, String space) {
        String[] houseLinks = value.split(",");
        for (String houseLink : houseLinks) {
            new House("", Type.getIdByName(type), Space.getIdByName(space), houseLink);
        }
    }

    public static ArrayList<House> getHouses(String type, String space) {

        ArrayList<House> houses = new ArrayList<>();
        for (int i = 0; i < House.houses.size(); i++) {
            try {
                if (House.houses.get(i).type.equals(type) && House.houses.get(i).space.equals(space))
                    houses.add(House.houses.get(i));
            }catch (NullPointerException ignore){}
        }
        return houses;
    }

    public static ArrayList<String> getTypeListBySpace(String space){
        ArrayList<String> TypeList = new ArrayList<>();
        for (House house : houses) {
            if(house.space.equals(space)&&!isRepeatedElement(house.type,TypeList))
                   TypeList.add(house.type);
        }
        return TypeList;
    }

    private static boolean isRepeatedElement(String type, ArrayList<String> typeList) {
        for (String houseType : typeList) {
            if (houseType.equals(type)) return true;
        }
        return false;
    }

    public static void DeleteAllInList() {
        houses.clear();
    }

    public String getName() {
        return name;
    }


    public String getRef() {
        return ref;
    }

}
