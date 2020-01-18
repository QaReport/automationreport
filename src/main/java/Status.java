public enum Status {
   CRITICAL("Critical"), MAJOR("Major"), MINOR("Minor");

   private String status;

   private Status(String status) {
        this.status = status;
    }

    @Override
    public String toString(){
        return status;
    }

}
