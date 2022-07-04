package com.danusys.web.commons.api.util;

import com.danusys.web.commons.api.dto.CctvDTO;
import com.danusys.web.commons.api.dto.LogicalfolderDTO;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2022/05/26
 * Time : 1:58 PM
 */
public class XmlDataUtil {

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

//            final Resource resource = new ClassPathResource(xml_data_path);
            final File file = new File(xml_data_path);
            final LogicalfolderDTO logicalfolder = (LogicalfolderDTO) unmarshaller.unmarshal(new FileReader(file)); // unmarshall 메소드 호출
            final LogicalfolderDTO.Logicalpoints logicalpoints = logicalfolder.getLogicalpoints();

            return logicalpoints.getLpts();
         } catch (JAXBException | IOException e) {
            e.printStackTrace();
        };
        return null;
    }

    public static CctvDTO getCctvInfo_test(String xmlString) {
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(CctvDTO.class); // JAXB Context 생성
            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller(); // Unmarshaller Object 생성

            final Resource resource = new ClassPathResource(xmlString);
            final CctvDTO cctvDTO = (CctvDTO) unmarshaller.unmarshal(new FileReader(resource.getFile())); // unmarshall 메소드 호출


            return cctvDTO;
        } catch (JAXBException | IOException e) {
            e.printStackTrace();
        };
        return null;
    }

    public static CctvDTO getCctvInfo(String xmlString) {
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(CctvDTO.class); // JAXB Context 생성
            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller(); // Unmarshaller Object 생성

//            final Resource resource = new ClassPathResource(xml_data_path);
            final CctvDTO cctvDTO = (CctvDTO) unmarshaller.unmarshal(new StringReader(xmlString)); // unmarshall 메소드 호출


            return cctvDTO;
        } catch (JAXBException e) {
            e.printStackTrace();
        };
        return null;
    }
}
