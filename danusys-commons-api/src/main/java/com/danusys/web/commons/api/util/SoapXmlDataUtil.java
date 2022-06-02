package com.danusys.web.commons.api.util;

import com.danusys.web.commons.api.dto.LogicalfolderDTO;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2022/05/26
 * Time : 1:58 PM
 */
public class SoapXmlDataUtil {

    /**
     * 광명 iot 솔루션 확산사업 정류장 포인트 목록
     * xml 파일에서 정보 가져오기
     * @param xml_data_path
     * @return
     */
    public static List<LogicalfolderDTO.Logicalpoints.Lpt> getGmSoapPostList(String xml_data_path) {
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(LogicalfolderDTO.class); // JAXB Context 생성
            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller(); // Unmarshaller Object 생성

            final Resource resource = new ClassPathResource(xml_data_path);
            final LogicalfolderDTO logicalfolder = (LogicalfolderDTO) unmarshaller.unmarshal(new FileReader(resource.getFile())); // unmarshall 메소드 호출
            final LogicalfolderDTO.Logicalpoints logicalpoints = logicalfolder.getLogicalpoints();

            return logicalpoints.getLpts();
         } catch (JAXBException | IOException e) {
            e.printStackTrace();
        };
        return null;
    }
}
