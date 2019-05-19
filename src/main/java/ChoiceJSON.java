class ChoiceJSON {

    private String type = null;
    private String space = null;

    public ChoiceJSON(String type, String space) {
        setValue(type, space);
    }

    public String getType() {
        return type;
    }

    public void setValue(String type, String space) {
        if (type == null && space == null) {
            this.type = null;
            this.space = null;
        } else {
            if (type != null) {
                this.type = type;
            }
            if (space != null) {
                this.space = space;
            }
        }
    }

    public String getSpace() {
        return space;
    }

    public boolean IsFull() {
        return this.type != null && this.space != null;

    }
}
