package repo;

import com.google.gson.Gson;
import entity.Client;
import entity.Company;
import exceptions.RepoException;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class ClientsRepo extends AbstractRepository<Long, Client>{

    private volatile static ClientsRepo instance;

    public void loadData() throws RepoException
    {
        super.elems.clear();
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("src/main/resources/stocks/clients.json"));
            Client[] clientsList = gson.fromJson(reader, Client[].class);
            reader.close();

            for(Client client: clientsList)
            {
                super.add(client);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private ClientsRepo() throws RepoException {
        loadData();
    }

    public static ClientsRepo getInstance() throws RepoException {
        ClientsRepo ref = instance;
        if (instance == null)
        {
            synchronized (ClientsRepo.class) {
                ref = instance;
                if (ref == null) {
                    instance = ref = new ClientsRepo();
                }
            }
        }
        return ref;
    }
}