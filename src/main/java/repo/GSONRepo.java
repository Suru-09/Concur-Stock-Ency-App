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

    public void loadData() throws RepoException
    {
        super.elems.clear();
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("src/main/resources/stocks/companies.json"));
            List<Company> companyList = Arrays.asList(gson.fromJson(reader, Company[].class));
            companyList.forEach(System.out::println);
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
