#
# Autogenerated by Thrift Compiler (0.9.0)
#
# DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
#
#  options string: py:new_style
#

from thrift.Thrift import TType, TMessageType, TException, TApplicationException

from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol, TProtocol
try:
  from thrift.protocol import fastbinary
except:
  fastbinary = None


class TNoiseType(object):
  Missing = 1
  Inconsistency = 2
  Outlier = 3
  Error = 4
  Duplicate = 5

  _VALUES_TO_NAMES = {
    1: "Missing",
    2: "Inconsistency",
    3: "Outlier",
    4: "Error",
    5: "Duplicate",
  }

  _NAMES_TO_VALUES = {
    "Missing": 1,
    "Inconsistency": 2,
    "Outlier": 3,
    "Error": 4,
    "Duplicate": 5,
  }

class TNoiseModel(object):
  Random = 1
  Histogram = 2

  _VALUES_TO_NAMES = {
    1: "Random",
    2: "Histogram",
  }

  _NAMES_TO_VALUES = {
    "Random": 1,
    "Histogram": 2,
  }


class TInputException(TException):
  """
  Attributes:
   - message
  """

  thrift_spec = (
    None, # 0
    (1, TType.STRING, 'message', None, None, ), # 1
  )

  def __init__(self, message=None,):
    self.message = message

  def read(self, iprot):
    if iprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and isinstance(iprot.trans, TTransport.CReadableTransport) and self.thrift_spec is not None and fastbinary is not None:
      fastbinary.decode_binary(self, iprot.trans, (self.__class__, self.thrift_spec))
      return
    iprot.readStructBegin()
    while True:
      (fname, ftype, fid) = iprot.readFieldBegin()
      if ftype == TType.STOP:
        break
      if fid == 1:
        if ftype == TType.STRING:
          self.message = iprot.readString();
        else:
          iprot.skip(ftype)
      else:
        iprot.skip(ftype)
      iprot.readFieldEnd()
    iprot.readStructEnd()

  def write(self, oprot):
    if oprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and self.thrift_spec is not None and fastbinary is not None:
      oprot.trans.write(fastbinary.encode_binary(self, (self.__class__, self.thrift_spec)))
      return
    oprot.writeStructBegin('TInputException')
    if self.message is not None:
      oprot.writeFieldBegin('message', TType.STRING, 1)
      oprot.writeString(self.message)
      oprot.writeFieldEnd()
    oprot.writeFieldStop()
    oprot.writeStructEnd()

  def validate(self):
    return


  def __str__(self):
    return repr(self)

  def __repr__(self):
    L = ['%s=%r' % (key, value)
      for key, value in self.__dict__.iteritems()]
    return '%s(%s)' % (self.__class__.__name__, ', '.join(L))

  def __eq__(self, other):
    return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

  def __ne__(self, other):
    return not (self == other)

class TQnoiseSpec(object):
  """
  Attributes:
   - noiseType
   - percentage
   - model
   - isOnCell
   - filteredColumns
   - numberOfSeed
   - distance
   - constraint
   - logfile
  """

  thrift_spec = (
    None, # 0
    (1, TType.I32, 'noiseType', None, None, ), # 1
    (2, TType.DOUBLE, 'percentage', None, None, ), # 2
    (3, TType.I32, 'model', None, None, ), # 3
    (4, TType.BOOL, 'isOnCell', None, None, ), # 4
    (5, TType.LIST, 'filteredColumns', (TType.STRING,None), None, ), # 5
    (6, TType.DOUBLE, 'numberOfSeed', None, None, ), # 6
    (7, TType.LIST, 'distance', (TType.DOUBLE,None), None, ), # 7
    (8, TType.LIST, 'constraint', (TType.STRING,None), None, ), # 8
    (9, TType.STRING, 'logfile', None, None, ), # 9
  )

  def __init__(self, noiseType=None, percentage=None, model=None, isOnCell=None, filteredColumns=None, numberOfSeed=None, distance=None, constraint=None, logfile=None,):
    self.noiseType = noiseType
    self.percentage = percentage
    self.model = model
    self.isOnCell = isOnCell
    self.filteredColumns = filteredColumns
    self.numberOfSeed = numberOfSeed
    self.distance = distance
    self.constraint = constraint
    self.logfile = logfile

  def read(self, iprot):
    if iprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and isinstance(iprot.trans, TTransport.CReadableTransport) and self.thrift_spec is not None and fastbinary is not None:
      fastbinary.decode_binary(self, iprot.trans, (self.__class__, self.thrift_spec))
      return
    iprot.readStructBegin()
    while True:
      (fname, ftype, fid) = iprot.readFieldBegin()
      if ftype == TType.STOP:
        break
      if fid == 1:
        if ftype == TType.I32:
          self.noiseType = iprot.readI32();
        else:
          iprot.skip(ftype)
      elif fid == 2:
        if ftype == TType.DOUBLE:
          self.percentage = iprot.readDouble();
        else:
          iprot.skip(ftype)
      elif fid == 3:
        if ftype == TType.I32:
          self.model = iprot.readI32();
        else:
          iprot.skip(ftype)
      elif fid == 4:
        if ftype == TType.BOOL:
          self.isOnCell = iprot.readBool();
        else:
          iprot.skip(ftype)
      elif fid == 5:
        if ftype == TType.LIST:
          self.filteredColumns = []
          (_etype3, _size0) = iprot.readListBegin()
          for _i4 in xrange(_size0):
            _elem5 = iprot.readString();
            self.filteredColumns.append(_elem5)
          iprot.readListEnd()
        else:
          iprot.skip(ftype)
      elif fid == 6:
        if ftype == TType.DOUBLE:
          self.numberOfSeed = iprot.readDouble();
        else:
          iprot.skip(ftype)
      elif fid == 7:
        if ftype == TType.LIST:
          self.distance = []
          (_etype9, _size6) = iprot.readListBegin()
          for _i10 in xrange(_size6):
            _elem11 = iprot.readDouble();
            self.distance.append(_elem11)
          iprot.readListEnd()
        else:
          iprot.skip(ftype)
      elif fid == 8:
        if ftype == TType.LIST:
          self.constraint = []
          (_etype15, _size12) = iprot.readListBegin()
          for _i16 in xrange(_size12):
            _elem17 = iprot.readString();
            self.constraint.append(_elem17)
          iprot.readListEnd()
        else:
          iprot.skip(ftype)
      elif fid == 9:
        if ftype == TType.STRING:
          self.logfile = iprot.readString();
        else:
          iprot.skip(ftype)
      else:
        iprot.skip(ftype)
      iprot.readFieldEnd()
    iprot.readStructEnd()

  def write(self, oprot):
    if oprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and self.thrift_spec is not None and fastbinary is not None:
      oprot.trans.write(fastbinary.encode_binary(self, (self.__class__, self.thrift_spec)))
      return
    oprot.writeStructBegin('TQnoiseSpec')
    if self.noiseType is not None:
      oprot.writeFieldBegin('noiseType', TType.I32, 1)
      oprot.writeI32(self.noiseType)
      oprot.writeFieldEnd()
    if self.percentage is not None:
      oprot.writeFieldBegin('percentage', TType.DOUBLE, 2)
      oprot.writeDouble(self.percentage)
      oprot.writeFieldEnd()
    if self.model is not None:
      oprot.writeFieldBegin('model', TType.I32, 3)
      oprot.writeI32(self.model)
      oprot.writeFieldEnd()
    if self.isOnCell is not None:
      oprot.writeFieldBegin('isOnCell', TType.BOOL, 4)
      oprot.writeBool(self.isOnCell)
      oprot.writeFieldEnd()
    if self.filteredColumns is not None:
      oprot.writeFieldBegin('filteredColumns', TType.LIST, 5)
      oprot.writeListBegin(TType.STRING, len(self.filteredColumns))
      for iter18 in self.filteredColumns:
        oprot.writeString(iter18)
      oprot.writeListEnd()
      oprot.writeFieldEnd()
    if self.numberOfSeed is not None:
      oprot.writeFieldBegin('numberOfSeed', TType.DOUBLE, 6)
      oprot.writeDouble(self.numberOfSeed)
      oprot.writeFieldEnd()
    if self.distance is not None:
      oprot.writeFieldBegin('distance', TType.LIST, 7)
      oprot.writeListBegin(TType.DOUBLE, len(self.distance))
      for iter19 in self.distance:
        oprot.writeDouble(iter19)
      oprot.writeListEnd()
      oprot.writeFieldEnd()
    if self.constraint is not None:
      oprot.writeFieldBegin('constraint', TType.LIST, 8)
      oprot.writeListBegin(TType.STRING, len(self.constraint))
      for iter20 in self.constraint:
        oprot.writeString(iter20)
      oprot.writeListEnd()
      oprot.writeFieldEnd()
    if self.logfile is not None:
      oprot.writeFieldBegin('logfile', TType.STRING, 9)
      oprot.writeString(self.logfile)
      oprot.writeFieldEnd()
    oprot.writeFieldStop()
    oprot.writeStructEnd()

  def validate(self):
    if self.noiseType is None:
      raise TProtocol.TProtocolException(message='Required field noiseType is unset!')
    if self.percentage is None:
      raise TProtocol.TProtocolException(message='Required field percentage is unset!')
    if self.model is None:
      raise TProtocol.TProtocolException(message='Required field model is unset!')
    return


  def __repr__(self):
    L = ['%s=%r' % (key, value)
      for key, value in self.__dict__.iteritems()]
    return '%s(%s)' % (self.__class__.__name__, ', '.join(L))

  def __eq__(self, other):
    return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

  def __ne__(self, other):
    return not (self == other)

class TQnoiseInput(object):
  """
  Attributes:
   - data
   - specs
   - header
   - type
  """

  thrift_spec = (
    None, # 0
    (1, TType.LIST, 'data', (TType.LIST,(TType.STRING,None)), None, ), # 1
    (2, TType.LIST, 'specs', (TType.STRUCT,(TQnoiseSpec, TQnoiseSpec.thrift_spec)), None, ), # 2
    (3, TType.LIST, 'header', (TType.STRING,None), None, ), # 3
    (4, TType.LIST, 'type', (TType.STRING,None), None, ), # 4
  )

  def __init__(self, data=None, specs=None, header=None, type=None,):
    self.data = data
    self.specs = specs
    self.header = header
    self.type = type

  def read(self, iprot):
    if iprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and isinstance(iprot.trans, TTransport.CReadableTransport) and self.thrift_spec is not None and fastbinary is not None:
      fastbinary.decode_binary(self, iprot.trans, (self.__class__, self.thrift_spec))
      return
    iprot.readStructBegin()
    while True:
      (fname, ftype, fid) = iprot.readFieldBegin()
      if ftype == TType.STOP:
        break
      if fid == 1:
        if ftype == TType.LIST:
          self.data = []
          (_etype24, _size21) = iprot.readListBegin()
          for _i25 in xrange(_size21):
            _elem26 = []
            (_etype30, _size27) = iprot.readListBegin()
            for _i31 in xrange(_size27):
              _elem32 = iprot.readString();
              _elem26.append(_elem32)
            iprot.readListEnd()
            self.data.append(_elem26)
          iprot.readListEnd()
        else:
          iprot.skip(ftype)
      elif fid == 2:
        if ftype == TType.LIST:
          self.specs = []
          (_etype36, _size33) = iprot.readListBegin()
          for _i37 in xrange(_size33):
            _elem38 = TQnoiseSpec()
            _elem38.read(iprot)
            self.specs.append(_elem38)
          iprot.readListEnd()
        else:
          iprot.skip(ftype)
      elif fid == 3:
        if ftype == TType.LIST:
          self.header = []
          (_etype42, _size39) = iprot.readListBegin()
          for _i43 in xrange(_size39):
            _elem44 = iprot.readString();
            self.header.append(_elem44)
          iprot.readListEnd()
        else:
          iprot.skip(ftype)
      elif fid == 4:
        if ftype == TType.LIST:
          self.type = []
          (_etype48, _size45) = iprot.readListBegin()
          for _i49 in xrange(_size45):
            _elem50 = iprot.readString();
            self.type.append(_elem50)
          iprot.readListEnd()
        else:
          iprot.skip(ftype)
      else:
        iprot.skip(ftype)
      iprot.readFieldEnd()
    iprot.readStructEnd()

  def write(self, oprot):
    if oprot.__class__ == TBinaryProtocol.TBinaryProtocolAccelerated and self.thrift_spec is not None and fastbinary is not None:
      oprot.trans.write(fastbinary.encode_binary(self, (self.__class__, self.thrift_spec)))
      return
    oprot.writeStructBegin('TQnoiseInput')
    if self.data is not None:
      oprot.writeFieldBegin('data', TType.LIST, 1)
      oprot.writeListBegin(TType.LIST, len(self.data))
      for iter51 in self.data:
        oprot.writeListBegin(TType.STRING, len(iter51))
        for iter52 in iter51:
          oprot.writeString(iter52)
        oprot.writeListEnd()
      oprot.writeListEnd()
      oprot.writeFieldEnd()
    if self.specs is not None:
      oprot.writeFieldBegin('specs', TType.LIST, 2)
      oprot.writeListBegin(TType.STRUCT, len(self.specs))
      for iter53 in self.specs:
        iter53.write(oprot)
      oprot.writeListEnd()
      oprot.writeFieldEnd()
    if self.header is not None:
      oprot.writeFieldBegin('header', TType.LIST, 3)
      oprot.writeListBegin(TType.STRING, len(self.header))
      for iter54 in self.header:
        oprot.writeString(iter54)
      oprot.writeListEnd()
      oprot.writeFieldEnd()
    if self.type is not None:
      oprot.writeFieldBegin('type', TType.LIST, 4)
      oprot.writeListBegin(TType.STRING, len(self.type))
      for iter55 in self.type:
        oprot.writeString(iter55)
      oprot.writeListEnd()
      oprot.writeFieldEnd()
    oprot.writeFieldStop()
    oprot.writeStructEnd()

  def validate(self):
    if self.data is None:
      raise TProtocol.TProtocolException(message='Required field data is unset!')
    if self.specs is None:
      raise TProtocol.TProtocolException(message='Required field specs is unset!')
    return


  def __repr__(self):
    L = ['%s=%r' % (key, value)
      for key, value in self.__dict__.iteritems()]
    return '%s(%s)' % (self.__class__.__name__, ', '.join(L))

  def __eq__(self, other):
    return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

  def __ne__(self, other):
    return not (self == other)