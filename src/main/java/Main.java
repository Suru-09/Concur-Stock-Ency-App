import exceptions.RepoException;
import repo.GSONRepo;

public class Main {
    public static void main(String[] argv) {
        GSONRepo repo = new GSONRepo();
        try {
            repo.loadData();
        } catch (RepoException ex) {
            ex.printStackTrace();
        }
    }
}