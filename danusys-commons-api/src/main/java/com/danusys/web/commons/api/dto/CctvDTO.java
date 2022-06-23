package com.danusys.web.commons.api.dto;

import lombok.Getter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2022/06/17
 * Time : 4:59 PM
 */
@Getter
@ToString
@XmlRootElement(name = "envelope", namespace = "http://schema.danusys.com/ngvms/envelope")
public class CctvDTO {
    @XmlElement(name = "GET_ALL_CENTERLSIT_RSP")
    private GetAllCenterListRsp getAllCenterListRsp;

    @Getter
    @ToString
    @XmlRootElement(name = "GET_ALL_CENTERLSIT_RSP")
    public static class GetAllCenterListRsp {
        @XmlAttribute(name = "count")
        private String count;

        @XmlElement(name = "StreamServerList")
        private StreamServerList streamServerList;

        @Getter
        @ToString
        @XmlRootElement(name = "StreamServerList")
        public static class StreamServerList {
            @XmlAttribute(name = "count")
            private String count;

            @XmlElement(name = "StreamServerInfo")
            private List<StreamServerInfo> streamServerInfo;

            @Getter
            @ToString
            @XmlRootElement(name = "StreamServerInfo")
            public static class StreamServerInfo {
                @XmlElement(name = "BaseInfo")
                private StreamServerBaseInfo baseInfo;

                @Getter
                @ToString
                @XmlRootElement(name = "BaseInfo")
                public static class StreamServerBaseInfo {
                    @XmlAttribute(name = "Name")
                    private String name;
                    @XmlAttribute(name = "Connected")
                    private String connected;
                    @XmlAttribute(name = "PORT")
                    private String PORT;
                    @XmlAttribute(name = "ID")
                    private String ID;
                    @XmlAttribute(name = "TYPE")
                    private String TYPE;
                    @XmlAttribute(name = "IP")
                    private String IP;
                }

                @XmlElement(name = "ServerType")
                private String serverType;
                @XmlElement(name = "ClusterID")
                private String clusterID;
                @XmlElement(name = "ServicePortRange")
                private ServicePortRange servicePortRange;

                @Getter
                @ToString
                @XmlRootElement(name = "ServicePortRange")
                public static class ServicePortRange {
                    @XmlAttribute(name = "MAX")
                    private String MAX;
                    @XmlAttribute(name = "MIN")
                    private String MIN;
                }

                @XmlElement(name = "StreamServerViewerPort")
                private String StreamServerViewerPort;
                @XmlElement(name = "UseOverLoad")
                private String UseOverLoad;
                @XmlElement(name = "UseFailOver")
                private String UseFailOver;
                @XmlElement(name = "MaxNodes")
                private String MaxNodes;

                @XmlElement(name = "CpuLoad")
                private CpuLoad cpuLoad;

                @Getter
                @ToString
                @XmlRootElement(name = "CpuLoad")
                public static class CpuLoad {
                    @XmlAttribute(name = "MAX")
                    private String MAX;
                    @XmlAttribute(name = "MIN")
                    private String MIN;
                }

                @XmlElement(name = "RamLoad")
                private RamLoad ramLoad;

                @Getter
                @ToString
                @XmlRootElement(name = "RamLoad")
                public static class RamLoad {
                    @XmlAttribute(name = "MAX")
                    private String MAX;
                    @XmlAttribute(name = "MIN")
                    private String MIN;
                }

                @XmlElement(name = "RecordLoad")
                private RecordLoad recordLoad;

                @Getter
                @ToString
                @XmlRootElement(name = "RecordLoad")
                public static class RecordLoad {
                    @XmlAttribute(name = "MAX")
                    private String MAX;
                    @XmlAttribute(name = "MIN")
                    private String MIN;
                }

                @XmlElement(name = "NetworkLoad")
                private NetworkLoad networkLoad;

                @Getter
                @ToString
                @XmlRootElement(name = "NetworkLoad")
                public static class NetworkLoad {
                    @XmlAttribute(name = "MAX")
                    private String MAX;
                    @XmlAttribute(name = "MIN")
                    private String MIN;
                }

                @XmlElement(name = "StoragePolicy")
                private String StoragePolicy;
                @XmlElement(name = "StorageServicePort")
                private String StorageServicePort;
                @XmlElement(name = "StorageCount")
                private String StorageCount;

                @XmlElement(name = "StorageInfoList")
                private StorageInfoList storageInfoList;

                @Getter
                @ToString
                @XmlRootElement(name = "StorageInfoList")
                public static class StorageInfoList {
                    @XmlElement(name = "StorageInfo")
                    private List<StorageInfo> storageInfo;

                    @Getter
                    @ToString
                    @XmlRootElement(name = "StorageInfo")
                    public static class StorageInfo {
                        @XmlElement(name = "StoragePath")
                        private String storagePath;
                        @XmlElement(name = "MaxDummyFileCount")
                        private String maxDummyFileCount;
                        @XmlElement(name = "MarginSpace")
                        private String marginSpace;
                        @XmlElement(name = "MaxWriteNodes")
                        private String maxWriteNodes;
                        @XmlElement(name = "MaxWriteRate")
                        private String maxWriteRate;
                        @XmlElement(name = "RecycleDummy")
                        private String recycleDummy;
                    }
                }

                @XmlElement(name = "MasterPath")
                private String MasterPath;
                @XmlElement(name = "ChildNodes")
                private List<ChildNode> childNodes;

                @Getter
                @ToString
                @XmlRootElement(name = "ChildNodes")
                public static class ChildNode {
                    @XmlElement(name = "ChildNodeID")
                    private List<String> childNodeID;
                }
            }
        }

        @XmlElement(name = "StreamNodeList")
        private StreamNodeList StreamNodeList;

        @Getter
        @ToString
        @XmlRootElement(name = "StreamNodeList")
        public static class StreamNodeList {
            @XmlAttribute(name = "count")
            private String count;

            @XmlElement(name = "StreamNodeInfo")
            private List<StreamNodeInfo> streamNodeInfo;

            @Getter
            @ToString
            @XmlRootElement(name = "StreamNodeInfo")
            public static class StreamNodeInfo {
                @XmlElement(name = "BaseInfo")
                private StreamNodeBaseInfo baseInfo;

                @Getter
                @ToString
                @XmlRootElement(name = "BaseInfo")
                public static class StreamNodeBaseInfo {
                    @XmlAttribute(name = "Name")
                    private String name;
                    @XmlAttribute(name = "Connected")
                    private String connected;
                    @XmlAttribute(name = "PORT")
                    private String PORT;
                    @XmlAttribute(name = "ID")
                    private String ID;
                    @XmlAttribute(name = "TYPE")
                    private String TYPE;
                    @XmlAttribute(name = "IP")
                    private String IP;
                    @XmlAttribute(name = "ManagementCode")
                    private String managementCode;
                }

                @XmlElement(name = "SourceInfo")
                private SourceInfo sourceInfo;

                @Getter
                @ToString
                @XmlRootElement(name = "SourceInfo")
                public static class SourceInfo {
                    @XmlElement(name = "SourceType")
                    private String sourceType;

                    @XmlElement(name = "CameraCount")
                    private String cameraCount;

                    @XmlElement(name = "ExternalSrcInfo")
                    private ExternalSrcInfo externalSrcInfo;

                    @Getter
                    @ToString
                    @XmlRootElement(name = "ExternalSrcInfo")
                    public static class ExternalSrcInfo {
                        @XmlElement(name = "VendorName")
                        private String vendorName;
                        @XmlElement(name = "ModelName")
                        private String modelName;
                        @XmlElement(name = "SourceIP")
                        private String sourceIP;
                        @XmlElement(name = "WebService")
                        private WebService webService;
                        @Getter
                        @ToString
                        @XmlRootElement(name = "WebService")
                        public static class WebService {
                            @XmlAttribute(name = "Password")
                            private String password;
                            @XmlAttribute(name = "ID")
                            private String ID;
                        }

                        @XmlElement(name = "property")
                        private List<Property> properties;

                        @Getter
                        @ToString
                        @XmlRootElement(name = "property")
                        public static class Property {
                            @XmlAttribute(name = "name")
                            private String name;
                            @XmlAttribute(name = "value")
                            private String value;
                        }
                        @XmlElement(name = "RelatedPTZNode")
                        private String relatedPTZNode;
                        @XmlElement(name = "CameraCount")
                        private String cameraCount;

                        //SubChannelList 필요한 데이터인지 확인 필요

                        @XmlElement(name = "TransCodingInfo")
                        private TransCodingInfo transCodingInfo;

                        @Getter
                        @ToString
                        @XmlRootElement(name = "TransCodingInfo")
                        public static class TransCodingInfo {
                            @XmlAttribute(name = "UseTransCoding")
                            private String useTransCoding;
                            @XmlElement(name = "CodecType")
                            private String codecType;
                            @XmlElement(name = "ResolutionX")
                            private String resolutionX;
                            @XmlElement(name = "ResolutionY")
                            private String resolutionY;
                            @XmlElement(name = "Quality")
                            private String quality;
                        }
                    }
                    //FacilityData 필요한 데이터인지 확인 필요
                    //TroopID 이하 데이터 매핑 안함 확인후 추가
                }
                @XmlElement(name = "Lat")
                private String latitude;

                @XmlElement(name = "Lon")
                private String longitude;

                @XmlElement(name = "UsePTZ")
                private String isPtz;

                @XmlElement(name = "SystemGroup")
                private String cctvPurpose;

                @XmlElement(name = "PTZPresetType")
                private String ptzType;
            }

        }

        @XmlElement(name = "ViewerList")
        private ViewerList viewerList;
        @Getter
        @ToString
        @XmlRootElement(name = "ViewerList")
        public static class ViewerList {
            @XmlAttribute(name = "count")
            private String count;
            @XmlElement(name = "ViewerInfo")
            private List<ViewerInfo> viewerInfo;

            @Getter
            @ToString
            @XmlRootElement(name = "ViewerInfo")
            public static class ViewerInfo {
                @XmlElement(name = "BaseInfo")
                private ViewerBaseInfo baseInfo;

                @Getter
                @ToString
                @XmlRootElement(name = "BaseInfo")
                public static class ViewerBaseInfo {
                    @XmlAttribute(name = "Name")
                    private String name;
                    @XmlAttribute(name = "Connected")
                    private String connected;
                    @XmlAttribute(name = "PORT")
                    private String PORT;
                    @XmlAttribute(name = "ID")
                    private String ID;
                    @XmlAttribute(name = "ManagementCode")
                    private String managementCode;
                    @XmlAttribute(name = "TYPE")
                    private String TYPE;
                    @XmlAttribute(name = "IP")
                    private String IP;
                }
            }
        }


        @XmlElement(name = "VMS_SVR_IP")
        private String VMS_SVR_IP;
    }
}
