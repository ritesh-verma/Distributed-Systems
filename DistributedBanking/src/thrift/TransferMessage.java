package thrift; /**
 * Autogenerated by Thrift Compiler (0.9.3)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;
import org.apache.thrift.scheme.TupleScheme;

import javax.annotation.Generated;
import java.util.*;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (0.9.3)", date = "2016-10-25")
public class TransferMessage implements org.apache.thrift.TBase<TransferMessage, TransferMessage._Fields>, java.io.Serializable, Cloneable, Comparable<TransferMessage> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TransferMessage");

  private static final org.apache.thrift.protocol.TField ORIG_BRANCH_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("orig_branchId", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField AMOUNT_FIELD_DESC = new org.apache.thrift.protocol.TField("amount", org.apache.thrift.protocol.TType.I32, (short)2);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TransferMessageStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TransferMessageTupleSchemeFactory());
  }

  public BranchID orig_branchId; // required
  public int amount; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    ORIG_BRANCH_ID((short)1, "orig_branchId"),
    AMOUNT((short)2, "amount");

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
        case 1: // ORIG_BRANCH_ID
          return ORIG_BRANCH_ID;
        case 2: // AMOUNT
          return AMOUNT;
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
  private static final int __AMOUNT_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.ORIG_BRANCH_ID, new org.apache.thrift.meta_data.FieldMetaData("orig_branchId", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, BranchID.class)));
    tmpMap.put(_Fields.AMOUNT, new org.apache.thrift.meta_data.FieldMetaData("amount", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TransferMessage.class, metaDataMap);
  }

  public TransferMessage() {
  }

  public TransferMessage(
    BranchID orig_branchId,
    int amount)
  {
    this();
    this.orig_branchId = orig_branchId;
    this.amount = amount;
    setAmountIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TransferMessage(TransferMessage other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetOrig_branchId()) {
      this.orig_branchId = new BranchID(other.orig_branchId);
    }
    this.amount = other.amount;
  }

  public TransferMessage deepCopy() {
    return new TransferMessage(this);
  }

  @Override
  public void clear() {
    this.orig_branchId = null;
    setAmountIsSet(false);
    this.amount = 0;
  }

  public BranchID getOrig_branchId() {
    return this.orig_branchId;
  }

  public TransferMessage setOrig_branchId(BranchID orig_branchId) {
    this.orig_branchId = orig_branchId;
    return this;
  }

  public void unsetOrig_branchId() {
    this.orig_branchId = null;
  }

  /** Returns true if field orig_branchId is set (has been assigned a value) and false otherwise */
  public boolean isSetOrig_branchId() {
    return this.orig_branchId != null;
  }

  public void setOrig_branchIdIsSet(boolean value) {
    if (!value) {
      this.orig_branchId = null;
    }
  }

  public int getAmount() {
    return this.amount;
  }

  public TransferMessage setAmount(int amount) {
    this.amount = amount;
    setAmountIsSet(true);
    return this;
  }

  public void unsetAmount() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __AMOUNT_ISSET_ID);
  }

  /** Returns true if field amount is set (has been assigned a value) and false otherwise */
  public boolean isSetAmount() {
    return EncodingUtils.testBit(__isset_bitfield, __AMOUNT_ISSET_ID);
  }

  public void setAmountIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __AMOUNT_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case ORIG_BRANCH_ID:
      if (value == null) {
        unsetOrig_branchId();
      } else {
        setOrig_branchId((BranchID)value);
      }
      break;

    case AMOUNT:
      if (value == null) {
        unsetAmount();
      } else {
        setAmount((Integer)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case ORIG_BRANCH_ID:
      return getOrig_branchId();

    case AMOUNT:
      return getAmount();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case ORIG_BRANCH_ID:
      return isSetOrig_branchId();
    case AMOUNT:
      return isSetAmount();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TransferMessage)
      return this.equals((TransferMessage)that);
    return false;
  }

  public boolean equals(TransferMessage that) {
    if (that == null)
      return false;

    boolean this_present_orig_branchId = true && this.isSetOrig_branchId();
    boolean that_present_orig_branchId = true && that.isSetOrig_branchId();
    if (this_present_orig_branchId || that_present_orig_branchId) {
      if (!(this_present_orig_branchId && that_present_orig_branchId))
        return false;
      if (!this.orig_branchId.equals(that.orig_branchId))
        return false;
    }

    boolean this_present_amount = true;
    boolean that_present_amount = true;
    if (this_present_amount || that_present_amount) {
      if (!(this_present_amount && that_present_amount))
        return false;
      if (this.amount != that.amount)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_orig_branchId = true && (isSetOrig_branchId());
    list.add(present_orig_branchId);
    if (present_orig_branchId)
      list.add(orig_branchId);

    boolean present_amount = true;
    list.add(present_amount);
    if (present_amount)
      list.add(amount);

    return list.hashCode();
  }

  @Override
  public int compareTo(TransferMessage other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetOrig_branchId()).compareTo(other.isSetOrig_branchId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetOrig_branchId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.orig_branchId, other.orig_branchId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetAmount()).compareTo(other.isSetAmount());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetAmount()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.amount, other.amount);
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
    StringBuilder sb = new StringBuilder("TransferMessage(");
    boolean first = true;

    sb.append("orig_branchId:");
    if (this.orig_branchId == null) {
      sb.append("null");
    } else {
      sb.append(this.orig_branchId);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("amount:");
    sb.append(this.amount);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
    if (orig_branchId != null) {
      orig_branchId.validate();
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
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class TransferMessageStandardSchemeFactory implements SchemeFactory {
    public TransferMessageStandardScheme getScheme() {
      return new TransferMessageStandardScheme();
    }
  }

  private static class TransferMessageStandardScheme extends StandardScheme<TransferMessage> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TransferMessage struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // ORIG_BRANCH_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.orig_branchId = new BranchID();
              struct.orig_branchId.read(iprot);
              struct.setOrig_branchIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // AMOUNT
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.amount = iprot.readI32();
              struct.setAmountIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, TransferMessage struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.orig_branchId != null) {
        oprot.writeFieldBegin(ORIG_BRANCH_ID_FIELD_DESC);
        struct.orig_branchId.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(AMOUNT_FIELD_DESC);
      oprot.writeI32(struct.amount);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TransferMessageTupleSchemeFactory implements SchemeFactory {
    public TransferMessageTupleScheme getScheme() {
      return new TransferMessageTupleScheme();
    }
  }

  private static class TransferMessageTupleScheme extends TupleScheme<TransferMessage> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TransferMessage struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetOrig_branchId()) {
        optionals.set(0);
      }
      if (struct.isSetAmount()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetOrig_branchId()) {
        struct.orig_branchId.write(oprot);
      }
      if (struct.isSetAmount()) {
        oprot.writeI32(struct.amount);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TransferMessage struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        struct.orig_branchId = new BranchID();
        struct.orig_branchId.read(iprot);
        struct.setOrig_branchIdIsSet(true);
      }
      if (incoming.get(1)) {
        struct.amount = iprot.readI32();
        struct.setAmountIsSet(true);
      }
    }
  }

}

