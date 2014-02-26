/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.external;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.thrift.TException;
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
    public List<List<String>> inject(TQnoiseInput param) throws TException {
        List<List<String>> data = param.getData();
        List<String> types = param.getType();
        List<TQnoiseSpec> specs = param.getSpecs();
        List<String> header = param.getHeader();

        Preconditions.checkArgument(data != null && data.size() > 0);
        Preconditions.checkArgument(types != null && types.size() > 0);
        Preconditions.checkArgument(specs != null && specs.size() > 0);
        Preconditions.checkArgument(header != null && header.size() > 0);

        List<DataType> dataTypes = Lists.newArrayList();
        for (String type : types)
            dataTypes.add(DataType.fromString(type));

        DataProfile profile = new DataProfile(data, header, dataTypes);

        List<NoiseSpec> specList = Lists.newArrayList();
        for (TQnoiseSpec spec : specs)
            specList.add(TQnoiseSpecConverter.convert(spec));
        QnoiseFacade.inject(profile, specList);
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
            server.serve();

        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }
}
