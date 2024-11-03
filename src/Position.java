public class Position {
    private int x;
    private int y;
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position pos = (Position) obj;
        return x == pos.x && y == pos.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }
    public void reverse(){
        y = 9 - y;
    }
    public void debugging(){
        System.out.printf("position: %d, %d%n", x, y);
    }
    public Position add(Position p){
        return new Position(x + p.x, y + p.y);
    }
    public Boolean checkPosition(){
        return x < 9 && y < 9 && x > 0 && y > 0;
    }
}
