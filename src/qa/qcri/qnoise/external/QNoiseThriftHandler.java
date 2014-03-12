/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.external;

import com.google.common.collect.Lists;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import qa.qcri.qnoise.QnoiseFacade;
import qa.qcri.qnoise.internal.DataProfile;
import qa.qcri.qnoise.internal.DataType;
import qa.qcri.qnoise.internal.NoiseSpec;

import java.util.List;

public class QNoiseThriftHandler implements TQnoise.Iface {
    private static final int port = 5434;
    private static TServer server;

    @Override
    public List<List<String>> inject(TQnoiseInput param) throws TInputException {
        List<List<String>> data = param.getData();
        List<TQnoiseSpec> specs = param.getSpecs();

        if (data == null || data.size() == 0)
            throw new TInputException("Input data cannot be null nor empty.");

        if (specs == null || specs.size() == 0)
            throw new TInputException("Specification cannot be null nor empty.");

        List<NoiseSpec> specList = Lists.newArrayList();
        for (TQnoiseSpec spec : specs) {
            NoiseSpec spec_ = TQnoiseSpecConverter.convert(spec);
            String errorMessage = QnoiseFacade.verfiy(spec_);
            if (errorMessage != null)
                throw new TInputException(errorMessage);
            specList.add(TQnoiseSpecConverter.convert(spec));
        }

        DataProfile profile;
        List<DataType> dataTypes = null;
        List<String> header = null;
        if (param.isSetHeader()) {
            header = param.getHeader();
        }

        if (param.isSetType()) {
            dataTypes = Lists.newArrayList();
            for (String type : param.getType())
                dataTypes.add(DataType.fromString(type));
        }

        if (dataTypes == null) {
            if (header == null)
                profile = new DataProfile(data);
            else
                profile = new DataProfile(data, header);
        } else if (header == null)
            profile = new DataProfile(data, dataTypes);
        else
            profile = new DataProfile(data, header, dataTypes);

        try {
            QnoiseFacade.inject(profile, specList);
        } catch (Exception ex) {
            throw new TInputException(ex.getMessage());
        }
        return profile.getData();
    }

    public static void main(String[] args) {
        try {
            QNoiseThriftHandler handler = new QNoiseThriftHandler();
            TQnoise.Processor processor =
                new TQnoise.Processor(handler);
            TServerTransport serverTransport = null;
            serverTransport = new TServerSocket(port);
            server =
                new TThreadPoolServer(
                    new TThreadPoolServer
                        .Args(serverTransport)
                        .processor(processor)
                );
            System.out.println("Let's make some noises...");
            server.serve();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }
}
