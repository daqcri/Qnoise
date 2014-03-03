#!/usr/bin/env python

from thrift import Thrift
from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol

import itertools
import urllib
import json

import ttypes
import TQnoise

Spec = ttypes.TQnoiseSpec
NoiseType = ttypes.TNoiseType
NoiseModel = ttypes.TNoiseModel

class ListTable(list):
    def _repr_html_(self):
        html = ["<table>"]
        for row in self:
            html.append("<tr>")

            for col in row:
                html.append("<td>{0}</td>".format(col))

            html.append("</tr>")
        html.append("</table>")
        return ''.join(html)

class Freebase(object):
    def __init__(self, api_key = None):
        self.__api_key = api_key
        self.__url = 'https://www.googleapis.com/freebase/v1/mqlread'

    def query(self, query_json):
        if query_json is None:
            print 'Query cannot be none.'
            return
        query = { 'query' : json.dumps(query_json)}
        query_url = self.__url + '?' + urllib.urlencode(query)
        response = json.loads(urllib.urlopen(query_url).read())
        return response['result']
        #if flatten:
        #    result = [item for sublist in result for item in sublist]
        #return result

class QnoiseClient(object):
    def __init__(self, server='localhost', port=5434):
        self.__server = server
        self.__port = port

    def tablify(self, data):
        return ListTable(data)

    def inject(self, data, specs, header=None, type=None):
        """
        Inject noises.
        """
        transport = None
        try:
            if data is None or specs is None:
                raise Thrift.TException("Data / Specs cannot be null nor empty.")

            if not isinstance(data[0], list):
                data = [data]

            if not isinstance(specs, list):
                specs = [specs]

            data = map(lambda x : map(lambda y : str(y), x), data)

            if header is None:
                if type is None:
                    input = ttypes.TQnoiseInput(data = data, specs = specs)
                else:
                    input = ttypes.TQnoiseInput(data = data, specs = specs, type = type)
            elif type is None:
                input = ttypes.TQnoiseInput(data = data, specs = specs, header = header)
            else:
                input = ttypes.TQnoiseInput(
                    data = data,
                    specs = specs,
                    header = header,
                    type = type)

            transport = TSocket.TSocket(self.__server, self.__port)
            transport = TTransport.TBufferedTransport(transport)
            protocol = TBinaryProtocol.TBinaryProtocol(transport)
            client = TQnoise.Client(protocol)
            transport.open()
            result = client.inject(input)
            return result
        except Thrift.TException, tx:
            print "Error: %s" % (tx.message)
        finally:
            if not transport is None:
                transport.close()



