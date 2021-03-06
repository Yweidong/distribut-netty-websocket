// source: proto/Message.proto
/**
 * @fileoverview
 * @enhanceable
 * @suppress {missingRequire} reports error on implicit type usages.
 * @suppress {messageConventions} JS Compiler reports an error if a variable or
 *     field starts with 'MSG_' and isn't a translatable message.
 * @public
 */
// GENERATED CODE -- DO NOT EDIT!
/* eslint-disable */
// @ts-nocheck

var jspb = require('google-protobuf');
var goog = jspb;
var global = Function('return this')();

goog.exportSymbol('proto.org.example.wsmessage.CmdType', null, global);
goog.exportSymbol('proto.org.example.wsmessage.MessageBody', null, global);
goog.exportSymbol('proto.org.example.wsmessage.Model', null, global);
/**
 * Generated by JsPbCodeGenerator.
 * @param {Array=} opt_data Optional initial data array, typically from a
 * server response, or constructed directly in Javascript. The array is used
 * in place and becomes part of the constructed object. It is not cloned.
 * If no data is provided, the constructed object will be empty, but still
 * valid.
 * @extends {jspb.Message}
 * @constructor
 */
proto.org.example.wsmessage.Model = function(opt_data) {
  jspb.Message.initialize(this, opt_data, 0, -1, null, null);
};
goog.inherits(proto.org.example.wsmessage.Model, jspb.Message);
if (goog.DEBUG && !COMPILED) {
  /**
   * @public
   * @override
   */
  proto.org.example.wsmessage.Model.displayName = 'proto.org.example.wsmessage.Model';
}
/**
 * Generated by JsPbCodeGenerator.
 * @param {Array=} opt_data Optional initial data array, typically from a
 * server response, or constructed directly in Javascript. The array is used
 * in place and becomes part of the constructed object. It is not cloned.
 * If no data is provided, the constructed object will be empty, but still
 * valid.
 * @extends {jspb.Message}
 * @constructor
 */
proto.org.example.wsmessage.MessageBody = function(opt_data) {
  jspb.Message.initialize(this, opt_data, 0, -1, null, null);
};
goog.inherits(proto.org.example.wsmessage.MessageBody, jspb.Message);
if (goog.DEBUG && !COMPILED) {
  /**
   * @public
   * @override
   */
  proto.org.example.wsmessage.MessageBody.displayName = 'proto.org.example.wsmessage.MessageBody';
}



if (jspb.Message.GENERATE_TO_OBJECT) {
/**
 * Creates an object representation of this proto.
 * Field names that are reserved in JavaScript and will be renamed to pb_name.
 * Optional fields that are not set will be set to undefined.
 * To access a reserved field use, foo.pb_<name>, eg, foo.pb_default.
 * For the list of reserved names please see:
 *     net/proto2/compiler/js/internal/generator.cc#kKeyword.
 * @param {boolean=} opt_includeInstance Deprecated. whether to include the
 *     JSPB instance for transitional soy proto support:
 *     http://goto/soy-param-migration
 * @return {!Object}
 */
proto.org.example.wsmessage.Model.prototype.toObject = function(opt_includeInstance) {
  return proto.org.example.wsmessage.Model.toObject(opt_includeInstance, this);
};


/**
 * Static version of the {@see toObject} method.
 * @param {boolean|undefined} includeInstance Deprecated. Whether to include
 *     the JSPB instance for transitional soy proto support:
 *     http://goto/soy-param-migration
 * @param {!proto.org.example.wsmessage.Model} msg The msg instance to transform.
 * @return {!Object}
 * @suppress {unusedLocalVariables} f is only used for nested messages
 */
proto.org.example.wsmessage.Model.toObject = function(includeInstance, msg) {
  var f, obj = {
    cmd: jspb.Message.getFieldWithDefault(msg, 1, 0),
    from: jspb.Message.getFieldWithDefault(msg, 2, ""),
    to: jspb.Message.getFieldWithDefault(msg, 3, ""),
    groupid: jspb.Message.getFieldWithDefault(msg, 4, ""),
    content: msg.getContent_asB64()
  };

  if (includeInstance) {
    obj.$jspbMessageInstance = msg;
  }
  return obj;
};
}


/**
 * Deserializes binary data (in protobuf wire format).
 * @param {jspb.ByteSource} bytes The bytes to deserialize.
 * @return {!proto.org.example.wsmessage.Model}
 */
proto.org.example.wsmessage.Model.deserializeBinary = function(bytes) {
  var reader = new jspb.BinaryReader(bytes);
  var msg = new proto.org.example.wsmessage.Model;
  return proto.org.example.wsmessage.Model.deserializeBinaryFromReader(msg, reader);
};


/**
 * Deserializes binary data (in protobuf wire format) from the
 * given reader into the given message object.
 * @param {!proto.org.example.wsmessage.Model} msg The message object to deserialize into.
 * @param {!jspb.BinaryReader} reader The BinaryReader to use.
 * @return {!proto.org.example.wsmessage.Model}
 */
proto.org.example.wsmessage.Model.deserializeBinaryFromReader = function(msg, reader) {
  while (reader.nextField()) {
    if (reader.isEndGroup()) {
      break;
    }
    var field = reader.getFieldNumber();
    switch (field) {
    case 1:
      var value = /** @type {!proto.org.example.wsmessage.CmdType} */ (reader.readEnum());
      msg.setCmd(value);
      break;
    case 2:
      var value = /** @type {string} */ (reader.readString());
      msg.setFrom(value);
      break;
    case 3:
      var value = /** @type {string} */ (reader.readString());
      msg.setTo(value);
      break;
    case 4:
      var value = /** @type {string} */ (reader.readString());
      msg.setGroupid(value);
      break;
    case 5:
      var value = /** @type {!Uint8Array} */ (reader.readBytes());
      msg.setContent(value);
      break;
    default:
      reader.skipField();
      break;
    }
  }
  return msg;
};


/**
 * Serializes the message to binary data (in protobuf wire format).
 * @return {!Uint8Array}
 */
proto.org.example.wsmessage.Model.prototype.serializeBinary = function() {
  var writer = new jspb.BinaryWriter();
  proto.org.example.wsmessage.Model.serializeBinaryToWriter(this, writer);
  return writer.getResultBuffer();
};


/**
 * Serializes the given message to binary data (in protobuf wire
 * format), writing to the given BinaryWriter.
 * @param {!proto.org.example.wsmessage.Model} message
 * @param {!jspb.BinaryWriter} writer
 * @suppress {unusedLocalVariables} f is only used for nested messages
 */
proto.org.example.wsmessage.Model.serializeBinaryToWriter = function(message, writer) {
  var f = undefined;
  f = message.getCmd();
  if (f !== 0.0) {
    writer.writeEnum(
      1,
      f
    );
  }
  f = message.getFrom();
  if (f.length > 0) {
    writer.writeString(
      2,
      f
    );
  }
  f = message.getTo();
  if (f.length > 0) {
    writer.writeString(
      3,
      f
    );
  }
  f = message.getGroupid();
  if (f.length > 0) {
    writer.writeString(
      4,
      f
    );
  }
  f = message.getContent_asU8();
  if (f.length > 0) {
    writer.writeBytes(
      5,
      f
    );
  }
};


/**
 * optional CmdType cmd = 1;
 * @return {!proto.org.example.wsmessage.CmdType}
 */
proto.org.example.wsmessage.Model.prototype.getCmd = function() {
  return /** @type {!proto.org.example.wsmessage.CmdType} */ (jspb.Message.getFieldWithDefault(this, 1, 0));
};


/**
 * @param {!proto.org.example.wsmessage.CmdType} value
 * @return {!proto.org.example.wsmessage.Model} returns this
 */
proto.org.example.wsmessage.Model.prototype.setCmd = function(value) {
  return jspb.Message.setProto3EnumField(this, 1, value);
};


/**
 * optional string from = 2;
 * @return {string}
 */
proto.org.example.wsmessage.Model.prototype.getFrom = function() {
  return /** @type {string} */ (jspb.Message.getFieldWithDefault(this, 2, ""));
};


/**
 * @param {string} value
 * @return {!proto.org.example.wsmessage.Model} returns this
 */
proto.org.example.wsmessage.Model.prototype.setFrom = function(value) {
  return jspb.Message.setProto3StringField(this, 2, value);
};


/**
 * optional string to = 3;
 * @return {string}
 */
proto.org.example.wsmessage.Model.prototype.getTo = function() {
  return /** @type {string} */ (jspb.Message.getFieldWithDefault(this, 3, ""));
};


/**
 * @param {string} value
 * @return {!proto.org.example.wsmessage.Model} returns this
 */
proto.org.example.wsmessage.Model.prototype.setTo = function(value) {
  return jspb.Message.setProto3StringField(this, 3, value);
};


/**
 * optional string groupId = 4;
 * @return {string}
 */
proto.org.example.wsmessage.Model.prototype.getGroupid = function() {
  return /** @type {string} */ (jspb.Message.getFieldWithDefault(this, 4, ""));
};


/**
 * @param {string} value
 * @return {!proto.org.example.wsmessage.Model} returns this
 */
proto.org.example.wsmessage.Model.prototype.setGroupid = function(value) {
  return jspb.Message.setProto3StringField(this, 4, value);
};


/**
 * optional bytes content = 5;
 * @return {!(string|Uint8Array)}
 */
proto.org.example.wsmessage.Model.prototype.getContent = function() {
  return /** @type {!(string|Uint8Array)} */ (jspb.Message.getFieldWithDefault(this, 5, ""));
};


/**
 * optional bytes content = 5;
 * This is a type-conversion wrapper around `getContent()`
 * @return {string}
 */
proto.org.example.wsmessage.Model.prototype.getContent_asB64 = function() {
  return /** @type {string} */ (jspb.Message.bytesAsB64(
      this.getContent()));
};


/**
 * optional bytes content = 5;
 * Note that Uint8Array is not supported on all browsers.
 * @see http://caniuse.com/Uint8Array
 * This is a type-conversion wrapper around `getContent()`
 * @return {!Uint8Array}
 */
proto.org.example.wsmessage.Model.prototype.getContent_asU8 = function() {
  return /** @type {!Uint8Array} */ (jspb.Message.bytesAsU8(
      this.getContent()));
};


/**
 * @param {!(string|Uint8Array)} value
 * @return {!proto.org.example.wsmessage.Model} returns this
 */
proto.org.example.wsmessage.Model.prototype.setContent = function(value) {
  return jspb.Message.setProto3BytesField(this, 5, value);
};





if (jspb.Message.GENERATE_TO_OBJECT) {
/**
 * Creates an object representation of this proto.
 * Field names that are reserved in JavaScript and will be renamed to pb_name.
 * Optional fields that are not set will be set to undefined.
 * To access a reserved field use, foo.pb_<name>, eg, foo.pb_default.
 * For the list of reserved names please see:
 *     net/proto2/compiler/js/internal/generator.cc#kKeyword.
 * @param {boolean=} opt_includeInstance Deprecated. whether to include the
 *     JSPB instance for transitional soy proto support:
 *     http://goto/soy-param-migration
 * @return {!Object}
 */
proto.org.example.wsmessage.MessageBody.prototype.toObject = function(opt_includeInstance) {
  return proto.org.example.wsmessage.MessageBody.toObject(opt_includeInstance, this);
};


/**
 * Static version of the {@see toObject} method.
 * @param {boolean|undefined} includeInstance Deprecated. Whether to include
 *     the JSPB instance for transitional soy proto support:
 *     http://goto/soy-param-migration
 * @param {!proto.org.example.wsmessage.MessageBody} msg The msg instance to transform.
 * @return {!Object}
 * @suppress {unusedLocalVariables} f is only used for nested messages
 */
proto.org.example.wsmessage.MessageBody.toObject = function(includeInstance, msg) {
  var f, obj = {
    title: jspb.Message.getFieldWithDefault(msg, 1, ""),
    content: jspb.Message.getFieldWithDefault(msg, 2, ""),
    time: jspb.Message.getFieldWithDefault(msg, 3, ""),
    type: jspb.Message.getFieldWithDefault(msg, 4, 0),
    extend: jspb.Message.getFieldWithDefault(msg, 5, "")
  };

  if (includeInstance) {
    obj.$jspbMessageInstance = msg;
  }
  return obj;
};
}


/**
 * Deserializes binary data (in protobuf wire format).
 * @param {jspb.ByteSource} bytes The bytes to deserialize.
 * @return {!proto.org.example.wsmessage.MessageBody}
 */
proto.org.example.wsmessage.MessageBody.deserializeBinary = function(bytes) {
  var reader = new jspb.BinaryReader(bytes);
  var msg = new proto.org.example.wsmessage.MessageBody;
  return proto.org.example.wsmessage.MessageBody.deserializeBinaryFromReader(msg, reader);
};


/**
 * Deserializes binary data (in protobuf wire format) from the
 * given reader into the given message object.
 * @param {!proto.org.example.wsmessage.MessageBody} msg The message object to deserialize into.
 * @param {!jspb.BinaryReader} reader The BinaryReader to use.
 * @return {!proto.org.example.wsmessage.MessageBody}
 */
proto.org.example.wsmessage.MessageBody.deserializeBinaryFromReader = function(msg, reader) {
  while (reader.nextField()) {
    if (reader.isEndGroup()) {
      break;
    }
    var field = reader.getFieldNumber();
    switch (field) {
    case 1:
      var value = /** @type {string} */ (reader.readString());
      msg.setTitle(value);
      break;
    case 2:
      var value = /** @type {string} */ (reader.readString());
      msg.setContent(value);
      break;
    case 3:
      var value = /** @type {string} */ (reader.readString());
      msg.setTime(value);
      break;
    case 4:
      var value = /** @type {number} */ (reader.readUint32());
      msg.setType(value);
      break;
    case 5:
      var value = /** @type {string} */ (reader.readString());
      msg.setExtend(value);
      break;
    default:
      reader.skipField();
      break;
    }
  }
  return msg;
};


/**
 * Serializes the message to binary data (in protobuf wire format).
 * @return {!Uint8Array}
 */
proto.org.example.wsmessage.MessageBody.prototype.serializeBinary = function() {
  var writer = new jspb.BinaryWriter();
  proto.org.example.wsmessage.MessageBody.serializeBinaryToWriter(this, writer);
  return writer.getResultBuffer();
};


/**
 * Serializes the given message to binary data (in protobuf wire
 * format), writing to the given BinaryWriter.
 * @param {!proto.org.example.wsmessage.MessageBody} message
 * @param {!jspb.BinaryWriter} writer
 * @suppress {unusedLocalVariables} f is only used for nested messages
 */
proto.org.example.wsmessage.MessageBody.serializeBinaryToWriter = function(message, writer) {
  var f = undefined;
  f = message.getTitle();
  if (f.length > 0) {
    writer.writeString(
      1,
      f
    );
  }
  f = message.getContent();
  if (f.length > 0) {
    writer.writeString(
      2,
      f
    );
  }
  f = message.getTime();
  if (f.length > 0) {
    writer.writeString(
      3,
      f
    );
  }
  f = message.getType();
  if (f !== 0) {
    writer.writeUint32(
      4,
      f
    );
  }
  f = message.getExtend();
  if (f.length > 0) {
    writer.writeString(
      5,
      f
    );
  }
};


/**
 * optional string title = 1;
 * @return {string}
 */
proto.org.example.wsmessage.MessageBody.prototype.getTitle = function() {
  return /** @type {string} */ (jspb.Message.getFieldWithDefault(this, 1, ""));
};


/**
 * @param {string} value
 * @return {!proto.org.example.wsmessage.MessageBody} returns this
 */
proto.org.example.wsmessage.MessageBody.prototype.setTitle = function(value) {
  return jspb.Message.setProto3StringField(this, 1, value);
};


/**
 * optional string content = 2;
 * @return {string}
 */
proto.org.example.wsmessage.MessageBody.prototype.getContent = function() {
  return /** @type {string} */ (jspb.Message.getFieldWithDefault(this, 2, ""));
};


/**
 * @param {string} value
 * @return {!proto.org.example.wsmessage.MessageBody} returns this
 */
proto.org.example.wsmessage.MessageBody.prototype.setContent = function(value) {
  return jspb.Message.setProto3StringField(this, 2, value);
};


/**
 * optional string time = 3;
 * @return {string}
 */
proto.org.example.wsmessage.MessageBody.prototype.getTime = function() {
  return /** @type {string} */ (jspb.Message.getFieldWithDefault(this, 3, ""));
};


/**
 * @param {string} value
 * @return {!proto.org.example.wsmessage.MessageBody} returns this
 */
proto.org.example.wsmessage.MessageBody.prototype.setTime = function(value) {
  return jspb.Message.setProto3StringField(this, 3, value);
};


/**
 * optional uint32 type = 4;
 * @return {number}
 */
proto.org.example.wsmessage.MessageBody.prototype.getType = function() {
  return /** @type {number} */ (jspb.Message.getFieldWithDefault(this, 4, 0));
};


/**
 * @param {number} value
 * @return {!proto.org.example.wsmessage.MessageBody} returns this
 */
proto.org.example.wsmessage.MessageBody.prototype.setType = function(value) {
  return jspb.Message.setProto3IntField(this, 4, value);
};


/**
 * optional string extend = 5;
 * @return {string}
 */
proto.org.example.wsmessage.MessageBody.prototype.getExtend = function() {
  return /** @type {string} */ (jspb.Message.getFieldWithDefault(this, 5, ""));
};


/**
 * @param {string} value
 * @return {!proto.org.example.wsmessage.MessageBody} returns this
 */
proto.org.example.wsmessage.MessageBody.prototype.setExtend = function(value) {
  return jspb.Message.setProto3StringField(this, 5, value);
};


/**
 * @enum {number}
 */
proto.org.example.wsmessage.CmdType = {
  WEBSOCKET_ONLINE: 0,
  WEBSOCKET_OFFLINE: 1,
  WEBSOCKET_HEARBEAT: 2,
  WEBSOCKET_DATA: 3,
  MESSAGE_NOTIFICATION: 4
};

goog.object.extend(exports, proto.org.example.wsmessage);
