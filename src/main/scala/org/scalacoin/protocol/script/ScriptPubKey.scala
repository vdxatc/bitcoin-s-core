package org.scalacoin.protocol.script

import org.scalacoin.marshallers.transaction.TransactionElement
import org.scalacoin.protocol._
import org.scalacoin.script.bitwise.{OP_EQUAL, OP_EQUALVERIFY}
import org.scalacoin.script.constant.{BytesToPushOntoStackImpl, ScriptConstantImpl, ScriptToken}
import org.scalacoin.script.crypto.{OP_CHECKSIG, OP_HASH160}
import org.scalacoin.script.stack.OP_DUP

/**
 * Created by chris on 12/26/15.
 */
sealed trait ScriptPubKey extends TransactionElement {

  /**
   * Representation of a scriptSignature in a parsed assembly format
   * this data structure can be run through the script interpreter to
   * see if a script evaluates to true
   * @return
   */
  def asm : Seq[ScriptToken]


  def reqSigs : Option[Int] = {
    addressType match {
      case P2PKH => Some(1)
      //TODO: Figure out how many signatures are actually required by the scriptPubKey
      case P2SH => None
      case NonStandard => None
    }
  }
  def addressType : AddressType = {
    asm match {
      case List(OP_DUP, OP_HASH160, BytesToPushOntoStackImpl(x), ScriptConstantImpl(pubKeyHash), OP_EQUALVERIFY, OP_CHECKSIG) => P2PKH
      case List(OP_HASH160, BytesToPushOntoStackImpl(x), ScriptConstantImpl(scriptHash), OP_EQUAL) => P2SH
      case _ => NonStandard
    }
  }

  //the addresses that the bitcoins correlated to the output
  def addresses : Seq[BitcoinAddress]

}


case class ScriptPubKeyImpl(asm : Seq[ScriptToken], hex : String, addresses : Seq[BitcoinAddress]) extends ScriptPubKey
