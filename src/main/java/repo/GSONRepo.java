package repo;

import com.google.gson.Gson;
import entity.Company;
import exceptions.RepoException;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class GSONRepo extends AbstractRepository<Long, Company>{

    private static GSONRepo instance;

    public void loadData() throws RepoException
    {
        super.elems.clear();
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("src/main/resources/stocks/companies.json"));
            Company[] companyList = gson.fromJson(reader, Company[].class);
            reader.close();

            for(Company comp: companyList)
            {
                super.add(comp);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private GSONRepo() throws RepoException {
        loadData();
    }

    public static GSONRepo getInstance() throws RepoException {
        if (instance != null)
        {
            return instance;
        }
        instance = new GSONRepo();
        return instance;
    }
}
