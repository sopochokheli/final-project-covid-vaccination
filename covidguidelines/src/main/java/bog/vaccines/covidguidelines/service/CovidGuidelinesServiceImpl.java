package bog.vaccines.covidguidelines.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
@Slf4j
public class CovidGuidelinesServiceImpl implements CovidGuidelinesService {

    @Value("${vaccines.guideline.dir}")
    private String dirName;

    @Override
    public String getGuideline(String vaccine) {
        try {
            return readFromFile(vaccine);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("couldn't read from file. exception: {}",e);
        }
        return null;
    }

    private String readFromFile(String fileName) throws IOException {
        File file = new File(dirName);
        String text  = "";
        String line;
        File[] files = file.listFiles();
        for(File f : files) {
            String[] split = f.getName().split("\\.");
            if(!f.isDirectory() && split.length != 0 && split[0].equals(fileName)) {
                BufferedReader br = new BufferedReader(new FileReader(f.getAbsolutePath()));
                while ((line = br.readLine()) != null) {
                    text = text + line +"\n";
                }
                br.close();
                return text;
            }
        }
        return null;
    }
}
