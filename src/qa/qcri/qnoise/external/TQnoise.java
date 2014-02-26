/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package qa.qcri.qnoise.external;

import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;
import org.apache.thrift.scheme.TupleScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class TQnoise {

  public interface Iface {

    public List<List<String>> inject(TQnoiseInput param) throws org.apache.thrift.TException;

  }

  public interface AsyncIface {

    public void inject(TQnoiseInput param, org.apache.thrift.async.AsyncMethodCallback<AsyncClient.inject_call> resultHandler) throws org.apache.thrift.TException;

  }

  public static class Client extends org.apache.thrift.TServiceClient implements Iface {
    public static class Factory implements org.apache.thrift.TServiceClientFactory<Client> {
      public Factory() {}
      public Client getClient(org.apache.thrift.protocol.TProtocol prot) {
        return new Client(prot);
      }
      public Client getClient(org.apache.thrift.protocol.TProtocol iprot, org.apache.thrift.protocol.TProtocol oprot) {
        return new Client(iprot, oprot);
      }
    }

    public Client(org.apache.thrift.protocol.TProtocol prot)
    {
      super(prot, prot);
    }

    public Client(org.apache.thrift.protocol.TProtocol iprot, org.apache.thrift.protocol.TProtocol oprot) {
      super(iprot, oprot);
    }

    public List<List<String>> inject(TQnoiseInput param) throws org.apache.thrift.TException
    {
      send_inject(param);
      return recv_inject();
    }

    public void send_inject(TQnoiseInput param) throws org.apache.thrift.TException
    {
      inject_args args = new inject_args();
      args.setParam(param);
      sendBase("inject", args);
    }

    public List<List<String>> recv_inject() throws org.apache.thrift.TException
    {
      inject_result result = new inject_result();
      receiveBase(result, "inject");
      if (result.isSetSuccess()) {
        return result.success;
      }
      throw new org.apache.thrift.TApplicationException(org.apache.thrift.TApplicationException.MISSING_RESULT, "inject failed: unknown result");
    }

  }
  public static class AsyncClient extends org.apache.thrift.async.TAsyncClient implements AsyncIface {
    public static class Factory implements org.apache.thrift.async.TAsyncClientFactory<AsyncClient> {
      private org.apache.thrift.async.TAsyncClientManager clientManager;
      private org.apache.thrift.protocol.TProtocolFactory protocolFactory;
      public Factory(org.apache.thrift.async.TAsyncClientManager clientManager, org.apache.thrift.protocol.TProtocolFactory protocolFactory) {
        this.clientManager = clientManager;
        this.protocolFactory = protocolFactory;
      }
      public AsyncClient getAsyncClient(org.apache.thrift.transport.TNonblockingTransport transport) {
        return new AsyncClient(protocolFactory, clientManager, transport);
      }
    }

    public AsyncClient(org.apache.thrift.protocol.TProtocolFactory protocolFactory, org.apache.thrift.async.TAsyncClientManager clientManager, org.apache.thrift.transport.TNonblockingTransport transport) {
      super(protocolFactory, clientManager, transport);
    }

    public void inject(TQnoiseInput param, org.apache.thrift.async.AsyncMethodCallback<inject_call> resultHandler) throws org.apache.thrift.TException {
      checkReady();
      inject_call method_call = new inject_call(param, resultHandler, this, ___protocolFactory, ___transport);
      this.___currentMethod = method_call;
      ___manager.call(method_call);
    }

    public static class inject_call extends org.apache.thrift.async.TAsyncMethodCall {
      private TQnoiseInput param;
      public inject_call(TQnoiseInput param, org.apache.thrift.async.AsyncMethodCallback<inject_call> resultHandler, org.apache.thrift.async.TAsyncClient client, org.apache.thrift.protocol.TProtocolFactory protocolFactory, org.apache.thrift.transport.TNonblockingTransport transport) throws org.apache.thrift.TException {
        super(client, protocolFactory, transport, resultHandler, false);
        this.param = param;
      }

      public void write_args(org.apache.thrift.protocol.TProtocol prot) throws org.apache.thrift.TException {
        prot.writeMessageBegin(new org.apache.thrift.protocol.TMessage("inject", org.apache.thrift.protocol.TMessageType.CALL, 0));
        inject_args args = new inject_args();
        args.setParam(param);
        args.write(prot);
        prot.writeMessageEnd();
      }

      public List<List<String>> getResult() throws org.apache.thrift.TException {
        if (getState() != org.apache.thrift.async.TAsyncMethodCall.State.RESPONSE_READ) {
          throw new IllegalStateException("Method call not finished!");
        }
        org.apache.thrift.transport.TMemoryInputTransport memoryTransport = new org.apache.thrift.transport.TMemoryInputTransport(getFrameBuffer().array());
        org.apache.thrift.protocol.TProtocol prot = client.getProtocolFactory().getProtocol(memoryTransport);
        return (new Client(prot)).recv_inject();
      }
    }

  }

  public static class Processor<I extends Iface> extends org.apache.thrift.TBaseProcessor<I> implements org.apache.thrift.TProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(Processor.class.getName());
    public Processor(I iface) {
      super(iface, getProcessMap(new HashMap<String, org.apache.thrift.ProcessFunction<I, ? extends org.apache.thrift.TBase>>()));
    }

    protected Processor(I iface, Map<String,  org.apache.thrift.ProcessFunction<I, ? extends  org.apache.thrift.TBase>> processMap) {
      super(iface, getProcessMap(processMap));
    }

    private static <I extends Iface> Map<String,  org.apache.thrift.ProcessFunction<I, ? extends  org.apache.thrift.TBase>> getProcessMap(Map<String,  org.apache.thrift.ProcessFunction<I, ? extends  org.apache.thrift.TBase>> processMap) {
      processMap.put("inject", new inject());
      return processMap;
    }

    public static class inject<I extends Iface> extends org.apache.thrift.ProcessFunction<I, inject_args> {
      public inject() {
        super("inject");
      }

      public inject_args getEmptyArgsInstance() {
        return new inject_args();
      }

      protected boolean isOneway() {
        return false;
      }

      public inject_result getResult(I iface, inject_args args) throws org.apache.thrift.TException {
        inject_result result = new inject_result();
        result.success = iface.inject(args.param);
        return result;
      }
    }

  }

  public static class inject_args implements org.apache.thrift.TBase<inject_args, inject_args._Fields>, java.io.Serializable, Cloneable   {
    private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("inject_args");

    private static final org.apache.thrift.protocol.TField PARAM_FIELD_DESC = new org.apache.thrift.protocol.TField("param", org.apache.thrift.protocol.TType.STRUCT, (short)1);

    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
    static {
      schemes.put(StandardScheme.class, new inject_argsStandardSchemeFactory());
      schemes.put(TupleScheme.class, new inject_argsTupleSchemeFactory());
    }

    private TQnoiseInput param; // required

    /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
    public enum _Fields implements org.apache.thrift.TFieldIdEnum {
      PARAM((short)1, "param");

      private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

      static {
        for (_Fields field : EnumSet.allOf(_Fields.class)) {
          byName.put(field.getFieldName(), field);
        }
      }

      /**
       * Find the _Fields constant that matches fieldId, or null if its not found.
       */
      public static _Fields findByThriftId(int fieldId) {
        switch(fieldId) {
          case 1: // PARAM
            return PARAM;
          default:
            return null;
        }
      }

      /**
       * Find the _Fields constant that matches fieldId, throwing an exception
       * if it is not found.
       */
      public static _Fields findByThriftIdOrThrow(int fieldId) {
        _Fields fields = findByThriftId(fieldId);
        if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
        return fields;
      }

      /**
       * Find the _Fields constant that matches name, or null if its not found.
       */
      public static _Fields findByName(String name) {
        return byName.get(name);
      }

      private final short _thriftId;
      private final String _fieldName;

      _Fields(short thriftId, String fieldName) {
        _thriftId = thriftId;
        _fieldName = fieldName;
      }

      public short getThriftFieldId() {
        return _thriftId;
      }

      public String getFieldName() {
        return _fieldName;
      }
    }

    // isset id assignments
    public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
    static {
      Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
      tmpMap.put(_Fields.PARAM, new org.apache.thrift.meta_data.FieldMetaData("param", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, TQnoiseInput.class)));
      metaDataMap = Collections.unmodifiableMap(tmpMap);
      org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(inject_args.class, metaDataMap);
    }

    public inject_args() {
    }

    public inject_args(
      TQnoiseInput param)
    {
      this();
      this.param = param;
    }

    /**
     * Performs a deep copy on <i>other</i>.
     */
    public inject_args(inject_args other) {
      if (other.isSetParam()) {
        this.param = new TQnoiseInput(other.param);
      }
    }

    public inject_args deepCopy() {
      return new inject_args(this);
    }

    @Override
    public void clear() {
      this.param = null;
    }

    public TQnoiseInput getParam() {
      return this.param;
    }

    public inject_args setParam(TQnoiseInput param) {
      this.param = param;
      return this;
    }

    public void unsetParam() {
      this.param = null;
    }

    /** Returns true if field param is set (has been assigned a value) and false otherwise */
    public boolean isSetParam() {
      return this.param != null;
    }

    public void setParamIsSet(boolean value) {
      if (!value) {
        this.param = null;
      }
    }

    public void setFieldValue(_Fields field, Object value) {
      switch (field) {
      case PARAM:
        if (value == null) {
          unsetParam();
        } else {
          setParam((TQnoiseInput)value);
        }
        break;

      }
    }

    public Object getFieldValue(_Fields field) {
      switch (field) {
      case PARAM:
        return getParam();

      }
      throw new IllegalStateException();
    }

    /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
    public boolean isSet(_Fields field) {
      if (field == null) {
        throw new IllegalArgumentException();
      }

      switch (field) {
      case PARAM:
        return isSetParam();
      }
      throw new IllegalStateException();
    }

    @Override
    public boolean equals(Object that) {
      if (that == null)
        return false;
      if (that instanceof inject_args)
        return this.equals((inject_args)that);
      return false;
    }

    public boolean equals(inject_args that) {
      if (that == null)
        return false;

      boolean this_present_param = true && this.isSetParam();
      boolean that_present_param = true && that.isSetParam();
      if (this_present_param || that_present_param) {
        if (!(this_present_param && that_present_param))
          return false;
        if (!this.param.equals(that.param))
          return false;
      }

      return true;
    }

    @Override
    public int hashCode() {
      return 0;
    }

    public int compareTo(inject_args other) {
      if (!getClass().equals(other.getClass())) {
        return getClass().getName().compareTo(other.getClass().getName());
      }

      int lastComparison = 0;
      inject_args typedOther = (inject_args)other;

      lastComparison = Boolean.valueOf(isSetParam()).compareTo(typedOther.isSetParam());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetParam()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.param, typedOther.param);
        if (lastComparison != 0) {
          return lastComparison;
        }
      }
      return 0;
    }

    public _Fields fieldForId(int fieldId) {
      return _Fields.findByThriftId(fieldId);
    }

    public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
      schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
      schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder("inject_args(");
      boolean first = true;

      sb.append("param:");
      if (this.param == null) {
        sb.append("null");
      } else {
        sb.append(this.param);
      }
      first = false;
      sb.append(")");
      return sb.toString();
    }

    public void validate() throws org.apache.thrift.TException {
      // check for required fields
      // check for sub-struct validity
      if (param != null) {
        param.validate();
      }
    }

    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
      try {
        write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
      } catch (org.apache.thrift.TException te) {
        throw new java.io.IOException(te);
      }
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
      try {
        read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
      } catch (org.apache.thrift.TException te) {
        throw new java.io.IOException(te);
      }
    }

    private static class inject_argsStandardSchemeFactory implements SchemeFactory {
      public inject_argsStandardScheme getScheme() {
        return new inject_argsStandardScheme();
      }
    }

    private static class inject_argsStandardScheme extends StandardScheme<inject_args> {

      public void read(org.apache.thrift.protocol.TProtocol iprot, inject_args struct) throws org.apache.thrift.TException {
        org.apache.thrift.protocol.TField schemeField;
        iprot.readStructBegin();
        while (true)
        {
          schemeField = iprot.readFieldBegin();
          if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
            break;
          }
          switch (schemeField.id) {
            case 1: // PARAM
              if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
                struct.param = new TQnoiseInput();
                struct.param.read(iprot);
                struct.setParamIsSet(true);
              } else { 
                org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
              }
              break;
            default:
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
          }
          iprot.readFieldEnd();
        }
        iprot.readStructEnd();

        // check for required fields of primitive type, which can't be checked in the validate method
        struct.validate();
      }

      public void write(org.apache.thrift.protocol.TProtocol oprot, inject_args struct) throws org.apache.thrift.TException {
        struct.validate();

        oprot.writeStructBegin(STRUCT_DESC);
        if (struct.param != null) {
          oprot.writeFieldBegin(PARAM_FIELD_DESC);
          struct.param.write(oprot);
          oprot.writeFieldEnd();
        }
        oprot.writeFieldStop();
        oprot.writeStructEnd();
      }

    }

    private static class inject_argsTupleSchemeFactory implements SchemeFactory {
      public inject_argsTupleScheme getScheme() {
        return new inject_argsTupleScheme();
      }
    }

    private static class inject_argsTupleScheme extends TupleScheme<inject_args> {

      @Override
      public void write(org.apache.thrift.protocol.TProtocol prot, inject_args struct) throws org.apache.thrift.TException {
        TTupleProtocol oprot = (TTupleProtocol) prot;
        BitSet optionals = new BitSet();
        if (struct.isSetParam()) {
          optionals.set(0);
        }
        oprot.writeBitSet(optionals, 1);
        if (struct.isSetParam()) {
          struct.param.write(oprot);
        }
      }

      @Override
      public void read(org.apache.thrift.protocol.TProtocol prot, inject_args struct) throws org.apache.thrift.TException {
        TTupleProtocol iprot = (TTupleProtocol) prot;
        BitSet incoming = iprot.readBitSet(1);
        if (incoming.get(0)) {
          struct.param = new TQnoiseInput();
          struct.param.read(iprot);
          struct.setParamIsSet(true);
        }
      }
    }

  }

  public static class inject_result implements org.apache.thrift.TBase<inject_result, inject_result._Fields>, java.io.Serializable, Cloneable   {
    private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("inject_result");

    private static final org.apache.thrift.protocol.TField SUCCESS_FIELD_DESC = new org.apache.thrift.protocol.TField("success", org.apache.thrift.protocol.TType.LIST, (short)0);

    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
    static {
      schemes.put(StandardScheme.class, new inject_resultStandardSchemeFactory());
      schemes.put(TupleScheme.class, new inject_resultTupleSchemeFactory());
    }

    private List<List<String>> success; // required

    /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
    public enum _Fields implements org.apache.thrift.TFieldIdEnum {
      SUCCESS((short)0, "success");

      private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

      static {
        for (_Fields field : EnumSet.allOf(_Fields.class)) {
          byName.put(field.getFieldName(), field);
        }
      }

      /**
       * Find the _Fields constant that matches fieldId, or null if its not found.
       */
      public static _Fields findByThriftId(int fieldId) {
        switch(fieldId) {
          case 0: // SUCCESS
            return SUCCESS;
          default:
            return null;
        }
      }

      /**
       * Find the _Fields constant that matches fieldId, throwing an exception
       * if it is not found.
       */
      public static _Fields findByThriftIdOrThrow(int fieldId) {
        _Fields fields = findByThriftId(fieldId);
        if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
        return fields;
      }

      /**
       * Find the _Fields constant that matches name, or null if its not found.
       */
      public static _Fields findByName(String name) {
        return byName.get(name);
      }

      private final short _thriftId;
      private final String _fieldName;

      _Fields(short thriftId, String fieldName) {
        _thriftId = thriftId;
        _fieldName = fieldName;
      }

      public short getThriftFieldId() {
        return _thriftId;
      }

      public String getFieldName() {
        return _fieldName;
      }
    }

    // isset id assignments
    public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
    static {
      Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
      tmpMap.put(_Fields.SUCCESS, new org.apache.thrift.meta_data.FieldMetaData("success", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
              new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
                  new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)))));
      metaDataMap = Collections.unmodifiableMap(tmpMap);
      org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(inject_result.class, metaDataMap);
    }

    public inject_result() {
    }

    public inject_result(
      List<List<String>> success)
    {
      this();
      this.success = success;
    }

    /**
     * Performs a deep copy on <i>other</i>.
     */
    public inject_result(inject_result other) {
      if (other.isSetSuccess()) {
        List<List<String>> __this__success = new ArrayList<List<String>>();
        for (List<String> other_element : other.success) {
          List<String> __this__success_copy = new ArrayList<String>();
          for (String other_element_element : other_element) {
            __this__success_copy.add(other_element_element);
          }
          __this__success.add(__this__success_copy);
        }
        this.success = __this__success;
      }
    }

    public inject_result deepCopy() {
      return new inject_result(this);
    }

    @Override
    public void clear() {
      this.success = null;
    }

    public int getSuccessSize() {
      return (this.success == null) ? 0 : this.success.size();
    }

    public java.util.Iterator<List<String>> getSuccessIterator() {
      return (this.success == null) ? null : this.success.iterator();
    }

    public void addToSuccess(List<String> elem) {
      if (this.success == null) {
        this.success = new ArrayList<List<String>>();
      }
      this.success.add(elem);
    }

    public List<List<String>> getSuccess() {
      return this.success;
    }

    public inject_result setSuccess(List<List<String>> success) {
      this.success = success;
      return this;
    }

    public void unsetSuccess() {
      this.success = null;
    }

    /** Returns true if field success is set (has been assigned a value) and false otherwise */
    public boolean isSetSuccess() {
      return this.success != null;
    }

    public void setSuccessIsSet(boolean value) {
      if (!value) {
        this.success = null;
      }
    }

    public void setFieldValue(_Fields field, Object value) {
      switch (field) {
      case SUCCESS:
        if (value == null) {
          unsetSuccess();
        } else {
          setSuccess((List<List<String>>)value);
        }
        break;

      }
    }

    public Object getFieldValue(_Fields field) {
      switch (field) {
      case SUCCESS:
        return getSuccess();

      }
      throw new IllegalStateException();
    }

    /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
    public boolean isSet(_Fields field) {
      if (field == null) {
        throw new IllegalArgumentException();
      }

      switch (field) {
      case SUCCESS:
        return isSetSuccess();
      }
      throw new IllegalStateException();
    }

    @Override
    public boolean equals(Object that) {
      if (that == null)
        return false;
      if (that instanceof inject_result)
        return this.equals((inject_result)that);
      return false;
    }

    public boolean equals(inject_result that) {
      if (that == null)
        return false;

      boolean this_present_success = true && this.isSetSuccess();
      boolean that_present_success = true && that.isSetSuccess();
      if (this_present_success || that_present_success) {
        if (!(this_present_success && that_present_success))
          return false;
        if (!this.success.equals(that.success))
          return false;
      }

      return true;
    }

    @Override
    public int hashCode() {
      return 0;
    }

    public int compareTo(inject_result other) {
      if (!getClass().equals(other.getClass())) {
        return getClass().getName().compareTo(other.getClass().getName());
      }

      int lastComparison = 0;
      inject_result typedOther = (inject_result)other;

      lastComparison = Boolean.valueOf(isSetSuccess()).compareTo(typedOther.isSetSuccess());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetSuccess()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.success, typedOther.success);
        if (lastComparison != 0) {
          return lastComparison;
        }
      }
      return 0;
    }

    public _Fields fieldForId(int fieldId) {
      return _Fields.findByThriftId(fieldId);
    }

    public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
      schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
      schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
      }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder("inject_result(");
      boolean first = true;

      sb.append("success:");
      if (this.success == null) {
        sb.append("null");
      } else {
        sb.append(this.success);
      }
      first = false;
      sb.append(")");
      return sb.toString();
    }

    public void validate() throws org.apache.thrift.TException {
      // check for required fields
      // check for sub-struct validity
    }

    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
      try {
        write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
      } catch (org.apache.thrift.TException te) {
        throw new java.io.IOException(te);
      }
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
      try {
        read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
      } catch (org.apache.thrift.TException te) {
        throw new java.io.IOException(te);
      }
    }

    private static class inject_resultStandardSchemeFactory implements SchemeFactory {
      public inject_resultStandardScheme getScheme() {
        return new inject_resultStandardScheme();
      }
    }

    private static class inject_resultStandardScheme extends StandardScheme<inject_result> {

      public void read(org.apache.thrift.protocol.TProtocol iprot, inject_result struct) throws org.apache.thrift.TException {
        org.apache.thrift.protocol.TField schemeField;
        iprot.readStructBegin();
        while (true)
        {
          schemeField = iprot.readFieldBegin();
          if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
            break;
          }
          switch (schemeField.id) {
            case 0: // SUCCESS
              if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
                {
                  org.apache.thrift.protocol.TList _list64 = iprot.readListBegin();
                  struct.success = new ArrayList<List<String>>(_list64.size);
                  for (int _i65 = 0; _i65 < _list64.size; ++_i65)
                  {
                    List<String> _elem66; // required
                    {
                      org.apache.thrift.protocol.TList _list67 = iprot.readListBegin();
                      _elem66 = new ArrayList<String>(_list67.size);
                      for (int _i68 = 0; _i68 < _list67.size; ++_i68)
                      {
                        String _elem69; // required
                        _elem69 = iprot.readString();
                        _elem66.add(_elem69);
                      }
                      iprot.readListEnd();
                    }
                    struct.success.add(_elem66);
                  }
                  iprot.readListEnd();
                }
                struct.setSuccessIsSet(true);
              } else { 
                org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
              }
              break;
            default:
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
          }
          iprot.readFieldEnd();
        }
        iprot.readStructEnd();

        // check for required fields of primitive type, which can't be checked in the validate method
        struct.validate();
      }

      public void write(org.apache.thrift.protocol.TProtocol oprot, inject_result struct) throws org.apache.thrift.TException {
        struct.validate();

        oprot.writeStructBegin(STRUCT_DESC);
        if (struct.success != null) {
          oprot.writeFieldBegin(SUCCESS_FIELD_DESC);
          {
            oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.LIST, struct.success.size()));
            for (List<String> _iter70 : struct.success)
            {
              {
                oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, _iter70.size()));
                for (String _iter71 : _iter70)
                {
                  oprot.writeString(_iter71);
                }
                oprot.writeListEnd();
              }
            }
            oprot.writeListEnd();
          }
          oprot.writeFieldEnd();
        }
        oprot.writeFieldStop();
        oprot.writeStructEnd();
      }

    }

    private static class inject_resultTupleSchemeFactory implements SchemeFactory {
      public inject_resultTupleScheme getScheme() {
        return new inject_resultTupleScheme();
      }
    }

    private static class inject_resultTupleScheme extends TupleScheme<inject_result> {

      @Override
      public void write(org.apache.thrift.protocol.TProtocol prot, inject_result struct) throws org.apache.thrift.TException {
        TTupleProtocol oprot = (TTupleProtocol) prot;
        BitSet optionals = new BitSet();
        if (struct.isSetSuccess()) {
          optionals.set(0);
        }
        oprot.writeBitSet(optionals, 1);
        if (struct.isSetSuccess()) {
          {
            oprot.writeI32(struct.success.size());
            for (List<String> _iter72 : struct.success)
            {
              {
                oprot.writeI32(_iter72.size());
                for (String _iter73 : _iter72)
                {
                  oprot.writeString(_iter73);
                }
              }
            }
          }
        }
      }

      @Override
      public void read(org.apache.thrift.protocol.TProtocol prot, inject_result struct) throws org.apache.thrift.TException {
        TTupleProtocol iprot = (TTupleProtocol) prot;
        BitSet incoming = iprot.readBitSet(1);
        if (incoming.get(0)) {
          {
            org.apache.thrift.protocol.TList _list74 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.LIST, iprot.readI32());
            struct.success = new ArrayList<List<String>>(_list74.size);
            for (int _i75 = 0; _i75 < _list74.size; ++_i75)
            {
              List<String> _elem76; // required
              {
                org.apache.thrift.protocol.TList _list77 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, iprot.readI32());
                _elem76 = new ArrayList<String>(_list77.size);
                for (int _i78 = 0; _i78 < _list77.size; ++_i78)
                {
                  String _elem79; // required
                  _elem79 = iprot.readString();
                  _elem76.add(_elem79);
                }
              }
              struct.success.add(_elem76);
            }
          }
          struct.setSuccessIsSet(true);
        }
      }
    }

  }

}