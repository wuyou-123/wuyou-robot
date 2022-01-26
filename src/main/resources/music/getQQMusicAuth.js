
// noinspection all

function getUrl(uin, pwd, code, verifysession, ptdrvs, sessionID) {
    var salt = uin2hex(uin);
    var p = getEncryption(pwd, salt, code, false);
    return "https://ssl.ptlogin2.qq.com/login?" +
        "u=" + uin +
        "&verifycode=" + code +
        "&pt_vcode_v1=0" +
        "&pt_verifysession_v1=" + verifysession +
        "&p=" + p +
        "&pt_randsalt=2" +
        "&u1=https%3A%2F%2Fgraph.qq.com%2Foauth2.0%2Flogin_jump" +
        "&ptredirect=0" +
        "&h=1&t=1&g=1&from_ui=1&ptlang=2052&action=1-1-1641452584192" +
        "&js_ver=21122814&js_type=1" +
        "&login_sig=u1cFxLxCIZyhQiuufGpUqedhK9g9VlQWIXW1ybpCg-G0-q9wd0mdzw3R9vNHFz2S" +
        "&pt_uistyle=40&aid=716027609" +
        "&daid=383&pt_3rd_aid=100497308" +
        "&ptdrvs=" + ptdrvs +
        "&sid=" + sessionID +
        "&has_onekey=1&"
}

RSA = function () {
    function parseBigInt(str, r) {
        return new BigInteger(str, r)
    }

    function linebrk(s, n) {
        var ret = "";
        var i = 0;
        while (i + n < s.length) {
            ret += s.substring(i, i + n) + "\n";
            i += n
        }
        return ret + s.substring(i, s.length)
    }

    function byte2Hex(b) {
        if (b < 16) return "0" + b.toString(16);
        else return b.toString(16)
    }

    function pkcs1pad2(s, n) {
        if (n < s.length + 11) {
            uv_alert("Message too long for RSA");
            return null
        }
        var ba = [];
        var i = s.length - 1;
        while (i >= 0 && n > 0) {
            ba[--n] = s.charCodeAt(i--)
        }
        ba[--n] = 0;
        var rng = new SecureRandom;
        var x = [];
        while (n > 2) {
            x[0] = 0;
            while (x[0] == 0) {
                rng.nextBytes(x)
            }
            ba[--n] = x[0]
        }
        ba[--n] = 2;
        ba[--n] = 0;
        return new BigInteger(ba)
    }

    function RSAKey() {
        this.n = null;
        this.e = 0;
        this.d = null;
        this.p = null;
        this.q = null;
        this.dmp1 = null;
        this.dmq1 = null;
        this.coeff = null
    }

    function RSASetPublic(N, E) {
        if (N != null && E != null && N.length > 0 && E.length > 0) {
            this.n = parseBigInt(N, 16);
            this.e = parseInt(E, 16)
        } else uv_alert("Invalid RSA public key")
    }

    function RSADoPublic(x) {
        return x.modPowInt(this.e, this.n)
    }

    function RSAEncrypt(text) {
        var m = pkcs1pad2(text, this.n.bitLength() + 7 >> 3);
        if (m == null) return null;
        var c = this.doPublic(m);
        if (c == null) return null;
        var h = c.toString(16);
        if ((h.length & 1) == 0) return h;
        else return "0" + h
    }

    RSAKey.prototype.doPublic = RSADoPublic;
    RSAKey.prototype.setPublic = RSASetPublic;
    RSAKey.prototype.encrypt = RSAEncrypt;
    var dbits;
    var canary = 0xdeadbeefcafe;
    var j_lm = (canary & 16777215) == 15715070;

    function BigInteger(a, b, c) {
        if (a != null)
            if ("number" == typeof a) this.fromNumber(a, b, c);
            else if (b == null && "string" != typeof a) this.fromString(a, 256);
            else this.fromString(a, b)
    }

    function nbi() {
        return new BigInteger(null)
    }

    function am1(i, x, w, j, c, n) {
        while (--n >= 0) {
            var v = x * this[i++] + w[j] + c;
            c = Math.floor(v / 67108864);
            w[j++] = v & 67108863
        }
        return c
    }

    function am2(i, x, w, j, c, n) {
        var xl = x & 32767,
            xh = x >> 15;
        while (--n >= 0) {
            var l = this[i] & 32767;
            var h = this[i++] >> 15;
            var m = xh * l + h * xl;
            l = xl * l + ((m & 32767) << 15) + w[j] + (c & 1073741823);
            c = (l >>> 30) + (m >>> 15) + xh * h + (c >>> 30);
            w[j++] = l & 1073741823
        }
        return c
    }

    function am3(i, x, w, j, c, n) {
        var xl = x & 16383,
            xh = x >> 14;
        while (--n >= 0) {
            var l = this[i] & 16383;
            var h = this[i++] >> 14;
            var m = xh * l + h * xl;
            l = xl * l + ((m & 16383) << 14) + w[j] + c;
            c = (l >> 28) + (m >> 14) + xh * h;
            w[j++] = l & 268435455
        }
        return c
    }

    BigInteger.prototype.am = am3;
    dbits = 28

    BigInteger.prototype.DB = dbits;
    BigInteger.prototype.DM = (1 << dbits) - 1;
    BigInteger.prototype.DV = 1 << dbits;
    var BI_FP = 52;
    BigInteger.prototype.FV = Math.pow(2, BI_FP);
    BigInteger.prototype.F1 = BI_FP - dbits;
    BigInteger.prototype.F2 = 2 * dbits - BI_FP;
    var BI_RM = "0123456789abcdefghijklmnopqrstuvwxyz";
    var BI_RC = [];
    var rr, vv;
    rr = "0".charCodeAt(0);
    for (vv = 0; vv <= 9; ++vv) {
        BI_RC[rr++] = vv
    }
    rr = "a".charCodeAt(0);
    for (vv = 10; vv < 36; ++vv) {
        BI_RC[rr++] = vv
    }
    rr = "A".charCodeAt(0);
    for (vv = 10; vv < 36; ++vv) {
        BI_RC[rr++] = vv
    }

    function int2char(n) {
        return BI_RM.charAt(n)
    }

    function intAt(s, i) {
        var c = BI_RC[s.charCodeAt(i)];
        return c == null ? -1 : c
    }

    function bnpCopyTo(r) {
        for (var i = this.t - 1; i >= 0; --i) {
            r[i] = this[i]
        }
        r.t = this.t;
        r.s = this.s
    }

    function bnpFromInt(x) {
        this.t = 1;
        this.s = x < 0 ? -1 : 0;
        if (x > 0) this[0] = x;
        else if (x < -1) this[0] = x + DV;
        else this.t = 0
    }

    function nbv(i) {
        var r = nbi();
        r.fromInt(i);
        return r
    }

    function bnpFromString(s, b) {
        var k;
        if (b == 16) k = 4;
        else if (b == 8) k = 3;
        else if (b == 256) k = 8;
        else if (b == 2) k = 1;
        else if (b == 32) k = 5;
        else if (b == 4) k = 2;
        else {
            this.fromRadix(s, b);
            return
        }
        this.t = 0;
        this.s = 0;
        var i = s.length,
            mi = false,
            sh = 0;
        while (--i >= 0) {
            var x = k == 8 ? s[i] & 255 : intAt(s, i);
            if (x < 0) {
                if (s.charAt(i) == "-") mi = true;
                continue
            }
            mi = false;
            if (sh == 0) this[this.t++] = x;
            else if (sh + k > this.DB) {
                this[this.t - 1] |= (x & (1 << this.DB - sh) - 1) << sh;
                this[this.t++] = x >> this.DB - sh
            } else this[this.t - 1] |= x << sh;
            sh += k;
            if (sh >= this.DB) sh -= this.DB
        }
        if (k == 8 && (s[0] & 128) != 0) {
            this.s = -1;
            if (sh > 0) this[this.t - 1] |= (1 << this.DB - sh) - 1 << sh
        }
        this.clamp();
        if (mi) BigInteger.ZERO.subTo(this, this)
    }

    function bnpClamp() {
        var c = this.s & this.DM;
        while (this.t > 0 && this[this.t - 1] == c) {
            --this.t
        }
    }

    function bnToString(b) {
        if (this.s < 0) return "-" + this.negate().toString(b);
        var k;
        if (b == 16) k = 4;
        else if (b == 8) k = 3;
        else if (b == 2) k = 1;
        else if (b == 32) k = 5;
        else if (b == 4) k = 2;
        else return this.toRadix(b);
        var km = (1 << k) - 1,
            d, m = false,
            r = "",
            i = this.t;
        var p = this.DB - i * this.DB % k;
        if (i-- > 0) {
            if (p < this.DB && (d = this[i] >> p) > 0) {
                m = true;
                r = int2char(d)
            }
            while (i >= 0) {
                if (p < k) {
                    d = (this[i] & (1 << p) - 1) << k - p;
                    d |= this[--i] >> (p += this.DB - k)
                } else {
                    d = this[i] >> (p -= k) & km;
                    if (p <= 0) {
                        p += this.DB;
                        --i
                    }
                }
                if (d > 0) m = true;
                if (m) r += int2char(d)
            }
        }
        return m ? r : "0"
    }

    function bnNegate() {
        var r = nbi();
        BigInteger.ZERO.subTo(this, r);
        return r
    }

    function bnAbs() {
        return this.s < 0 ? this.negate() : this
    }

    function bnCompareTo(a) {
        var r = this.s - a.s;
        if (r != 0) return r;
        var i = this.t;
        r = i - a.t;
        if (r != 0) return r;
        while (--i >= 0) {
            if ((r = this[i] - a[i]) != 0) return r
        }
        return 0
    }

    function nbits(x) {
        var r = 1,
            t;
        if ((t = x >>> 16) != 0) {
            x = t;
            r += 16
        }
        if ((t = x >> 8) != 0) {
            x = t;
            r += 8
        }
        if ((t = x >> 4) != 0) {
            x = t;
            r += 4
        }
        if ((t = x >> 2) != 0) {
            x = t;
            r += 2
        }
        if ((t = x >> 1) != 0) {
            x = t;
            r += 1
        }
        return r
    }

    function bnBitLength() {
        if (this.t <= 0) return 0;
        return this.DB * (this.t - 1) + nbits(this[this.t - 1] ^ this.s & this.DM)
    }

    function bnpDLShiftTo(n, r) {
        var i;
        for (i = this.t - 1; i >= 0; --i) {
            r[i + n] = this[i]
        }
        for (i = n - 1; i >= 0; --i) {
            r[i] = 0
        }
        r.t = this.t + n;
        r.s = this.s
    }

    function bnpDRShiftTo(n, r) {
        for (var i = n; i < this.t; ++i) {
            r[i - n] = this[i]
        }
        r.t = Math.max(this.t - n, 0);
        r.s = this.s
    }

    function bnpLShiftTo(n, r) {
        var bs = n % this.DB;
        var cbs = this.DB - bs;
        var bm = (1 << cbs) - 1;
        var ds = Math.floor(n / this.DB),
            c = this.s << bs & this.DM,
            i;
        for (i = this.t - 1; i >= 0; --i) {
            r[i + ds + 1] = this[i] >> cbs | c;
            c = (this[i] & bm) << bs
        }
        for (i = ds - 1; i >= 0; --i) {
            r[i] = 0
        }
        r[ds] = c;
        r.t = this.t + ds + 1;
        r.s = this.s;
        r.clamp()
    }

    function bnpRShiftTo(n, r) {
        r.s = this.s;
        var ds = Math.floor(n / this.DB);
        if (ds >= this.t) {
            r.t = 0;
            return
        }
        var bs = n % this.DB;
        var cbs = this.DB - bs;
        var bm = (1 << bs) - 1;
        r[0] = this[ds] >> bs;
        for (var i = ds + 1; i < this.t; ++i) {
            r[i - ds - 1] |= (this[i] & bm) << cbs;
            r[i - ds] = this[i] >> bs
        }
        if (bs > 0) r[this.t - ds - 1] |= (this.s & bm) << cbs;
        r.t = this.t - ds;
        r.clamp()
    }

    function bnpSubTo(a, r) {
        var i = 0,
            c = 0,
            m = Math.min(a.t, this.t);
        while (i < m) {
            c += this[i] - a[i];
            r[i++] = c & this.DM;
            c >>= this.DB
        }
        if (a.t < this.t) {
            c -= a.s;
            while (i < this.t) {
                c += this[i];
                r[i++] = c & this.DM;
                c >>= this.DB
            }
            c += this.s
        } else {
            c += this.s;
            while (i < a.t) {
                c -= a[i];
                r[i++] = c & this.DM;
                c >>= this.DB
            }
            c -= a.s
        }
        r.s = c < 0 ? -1 : 0;
        if (c < -1) r[i++] = this.DV + c;
        else if (c > 0) r[i++] = c;
        r.t = i;
        r.clamp()
    }

    function bnpMultiplyTo(a, r) {
        var x = this.abs(),
            y = a.abs();
        var i = x.t;
        r.t = i + y.t;
        while (--i >= 0) {
            r[i] = 0
        }
        for (i = 0; i < y.t; ++i) {
            r[i + x.t] = x.am(0, y[i], r, i, 0, x.t)
        }
        r.s = 0;
        r.clamp();
        if (this.s != a.s) BigInteger.ZERO.subTo(r, r)
    }

    function bnpSquareTo(r) {
        var x = this.abs();
        var i = r.t = 2 * x.t;
        while (--i >= 0) {
            r[i] = 0
        }
        for (i = 0; i < x.t - 1; ++i) {
            var c = x.am(i, x[i], r, 2 * i, 0, 1);
            if ((r[i + x.t] += x.am(i + 1, 2 * x[i], r, 2 * i + 1, c, x.t - i - 1)) >= x.DV) {
                r[i + x.t] -= x.DV;
                r[i + x.t + 1] = 1
            }
        }
        if (r.t > 0) r[r.t - 1] += x.am(i, x[i], r, 2 * i, 0, 1);
        r.s = 0;
        r.clamp()
    }

    function bnpDivRemTo(m, q, r) {
        var pm = m.abs();
        if (pm.t <= 0) return;
        var pt = this.abs();
        if (pt.t < pm.t) {
            if (q != null) q.fromInt(0);
            if (r != null) this.copyTo(r);
            return
        }
        if (r == null) r = nbi();
        var y = nbi(),
            ts = this.s,
            ms = m.s;
        var nsh = this.DB - nbits(pm[pm.t - 1]);
        if (nsh > 0) {
            pm.lShiftTo(nsh, y);
            pt.lShiftTo(nsh, r)
        } else {
            pm.copyTo(y);
            pt.copyTo(r)
        }
        var ys = y.t;
        var y0 = y[ys - 1];
        if (y0 == 0) return;
        var yt = y0 * (1 << this.F1) + (ys > 1 ? y[ys - 2] >> this.F2 : 0);
        var d1 = this.FV / yt,
            d2 = (1 << this.F1) / yt,
            e = 1 << this.F2;
        var i = r.t,
            j = i - ys,
            t = q == null ? nbi() : q;
        y.dlShiftTo(j, t);
        if (r.compareTo(t) >= 0) {
            r[r.t++] = 1;
            r.subTo(t, r)
        }
        BigInteger.ONE.dlShiftTo(ys, t);
        t.subTo(y, y);
        while (y.t < ys) {
            y[y.t++] = 0
        }
        while (--j >= 0) {
            var qd = r[--i] == y0 ? this.DM : Math.floor(r[i] * d1 + (r[i - 1] + e) * d2);
            if ((r[i] += y.am(0, qd, r, j, 0, ys)) < qd) {
                y.dlShiftTo(j, t);
                r.subTo(t, r);
                while (r[i] < --qd) {
                    r.subTo(t, r)
                }
            }
        }
        if (q != null) {
            r.drShiftTo(ys, q);
            if (ts != ms) BigInteger.ZERO.subTo(q, q)
        }
        r.t = ys;
        r.clamp();
        if (nsh > 0) r.rShiftTo(nsh, r);
        if (ts < 0) BigInteger.ZERO.subTo(r, r)
    }

    function bnMod(a) {
        var r = nbi();
        this.abs().divRemTo(a, null, r);
        if (this.s < 0 && r.compareTo(BigInteger.ZERO) > 0) a.subTo(r, r);
        return r
    }

    function Classic(m) {
        this.m = m
    }

    function cConvert(x) {
        if (x.s < 0 || x.compareTo(this.m) >= 0) return x.mod(this.m);
        else return x
    }

    function cRevert(x) {
        return x
    }

    function cReduce(x) {
        x.divRemTo(this.m, null, x)
    }

    function cMulTo(x, y, r) {
        x.multiplyTo(y, r);
        this.reduce(r)
    }

    function cSqrTo(x, r) {
        x.squareTo(r);
        this.reduce(r)
    }

    Classic.prototype.convert = cConvert;
    Classic.prototype.revert = cRevert;
    Classic.prototype.reduce = cReduce;
    Classic.prototype.mulTo = cMulTo;
    Classic.prototype.sqrTo = cSqrTo;

    function bnpInvDigit() {
        if (this.t < 1) return 0;
        var x = this[0];
        if ((x & 1) == 0) return 0;
        var y = x & 3;
        y = y * (2 - (x & 15) * y) & 15;
        y = y * (2 - (x & 255) * y) & 255;
        y = y * (2 - ((x & 65535) * y & 65535)) & 65535;
        y = y * (2 - x * y % this.DV) % this.DV;
        return y > 0 ? this.DV - y : -y
    }

    function Montgomery(m) {
        this.m = m;
        this.mp = m.invDigit();
        this.mpl = this.mp & 32767;
        this.mph = this.mp >> 15;
        this.um = (1 << m.DB - 15) - 1;
        this.mt2 = 2 * m.t
    }

    function montConvert(x) {
        var r = nbi();
        x.abs().dlShiftTo(this.m.t, r);
        r.divRemTo(this.m, null, r);
        if (x.s < 0 && r.compareTo(BigInteger.ZERO) > 0) this.m.subTo(r, r);
        return r
    }

    function montRevert(x) {
        var r = nbi();
        x.copyTo(r);
        this.reduce(r);
        return r
    }

    function montReduce(x) {
        while (x.t <= this.mt2) {
            x[x.t++] = 0
        }
        for (var i = 0; i < this.m.t; ++i) {
            var j = x[i] & 32767;
            var u0 = j * this.mpl + ((j * this.mph + (x[i] >> 15) * this.mpl & this.um) << 15) & x.DM;
            j = i + this.m.t;
            x[j] += this.m.am(0, u0, x, i, 0, this.m.t);
            while (x[j] >= x.DV) {
                x[j] -= x.DV;
                x[++j]++
            }
        }
        x.clamp();
        x.drShiftTo(this.m.t, x);
        if (x.compareTo(this.m) >= 0) x.subTo(this.m, x)
    }

    function montSqrTo(x, r) {
        x.squareTo(r);
        this.reduce(r)
    }

    function montMulTo(x, y, r) {
        x.multiplyTo(y, r);
        this.reduce(r)
    }

    Montgomery.prototype.convert = montConvert;
    Montgomery.prototype.revert = montRevert;
    Montgomery.prototype.reduce = montReduce;
    Montgomery.prototype.mulTo = montMulTo;
    Montgomery.prototype.sqrTo = montSqrTo;

    function bnpIsEven() {
        return (this.t > 0 ? this[0] & 1 : this.s) == 0
    }

    function bnpExp(e, z) {
        if (e > 4294967295 || e < 1) return BigInteger.ONE;
        var r = nbi(),
            r2 = nbi(),
            g = z.convert(this),
            i = nbits(e) - 1;
        g.copyTo(r);
        while (--i >= 0) {
            z.sqrTo(r, r2);
            if ((e & 1 << i) > 0) z.mulTo(r2, g, r);
            else {
                var t = r;
                r = r2;
                r2 = t
            }
        }
        return z.revert(r)
    }

    function bnModPowInt(e, m) {
        var z;
        if (e < 256 || m.isEven()) z = new Classic(m);
        else z = new Montgomery(m);
        return this.exp(e, z)
    }

    BigInteger.prototype.copyTo = bnpCopyTo;
    BigInteger.prototype.fromInt = bnpFromInt;
    BigInteger.prototype.fromString = bnpFromString;
    BigInteger.prototype.clamp = bnpClamp;
    BigInteger.prototype.dlShiftTo = bnpDLShiftTo;
    BigInteger.prototype.drShiftTo = bnpDRShiftTo;
    BigInteger.prototype.lShiftTo = bnpLShiftTo;
    BigInteger.prototype.rShiftTo = bnpRShiftTo;
    BigInteger.prototype.subTo = bnpSubTo;
    BigInteger.prototype.multiplyTo = bnpMultiplyTo;
    BigInteger.prototype.squareTo = bnpSquareTo;
    BigInteger.prototype.divRemTo = bnpDivRemTo;
    BigInteger.prototype.invDigit = bnpInvDigit;
    BigInteger.prototype.isEven = bnpIsEven;
    BigInteger.prototype.exp = bnpExp;
    BigInteger.prototype.toString = bnToString;
    BigInteger.prototype.negate = bnNegate;
    BigInteger.prototype.abs = bnAbs;
    BigInteger.prototype.compareTo = bnCompareTo;
    BigInteger.prototype.bitLength = bnBitLength;
    BigInteger.prototype.mod = bnMod;
    BigInteger.prototype.modPowInt = bnModPowInt;
    BigInteger.ZERO = nbv(0);
    BigInteger.ONE = nbv(1);
    var rng_state;
    var rng_pool;
    var rng_pptr;

    function rng_seed_int(x) {
        rng_pool[rng_pptr++] ^= x & 255;
        rng_pool[rng_pptr++] ^= x >> 8 & 255;
        rng_pool[rng_pptr++] ^= x >> 16 & 255;
        rng_pool[rng_pptr++] ^= x >> 24 & 255;
        if (rng_pptr >= rng_psize) rng_pptr -= rng_psize
    }

    function rng_seed_time() {
        rng_seed_int(new Date().getTime())
    }

    if (rng_pool == null) {
        rng_pool = [];
        rng_pptr = 0;
        var t;
        while (rng_pptr < rng_psize) {
            t = Math.floor(65536 * Math.random());
            rng_pool[rng_pptr++] = t >>> 8;
            rng_pool[rng_pptr++] = t & 255
        }
        rng_pptr = 0;
        rng_seed_time()
    }

    function rng_get_byte() {
        if (rng_state == null) {
            rng_seed_time();
            rng_state = prng_newstate();
            rng_state.init(rng_pool);
            for (rng_pptr = 0; rng_pptr < rng_pool.length; ++rng_pptr) {
                rng_pool[rng_pptr] = 0
            }
            rng_pptr = 0
        }
        return rng_state.next()
    }

    function rng_get_bytes(ba) {
        var i;
        for (i = 0; i < ba.length; ++i) {
            ba[i] = rng_get_byte()
        }
    }

    function SecureRandom() {
    }

    SecureRandom.prototype.nextBytes = rng_get_bytes;

    function Arcfour() {
        this.i = 0;
        this.j = 0;
        this.S = []
    }

    function ARC4init(key) {
        var i, j, t;
        for (i = 0; i < 256; ++i) {
            this.S[i] = i
        }
        j = 0;
        for (i = 0; i < 256; ++i) {
            j = j + this.S[i] + key[i % key.length] & 255;
            t = this.S[i];
            this.S[i] = this.S[j];
            this.S[j] = t
        }
        this.i = 0;
        this.j = 0
    }

    function ARC4next() {
        var t;
        this.i = this.i + 1 & 255;
        this.j = this.j + this.S[this.i] & 255;
        t = this.S[this.i];
        this.S[this.i] = this.S[this.j];
        this.S[this.j] = t;
        return this.S[t + this.S[this.i] & 255]
    }

    Arcfour.prototype.init = ARC4init;
    Arcfour.prototype.next = ARC4next;

    function prng_newstate() {
        return new Arcfour
    }

    var rng_psize = 256;

    function rsa_encrypt(rawValue, key, mod) {
        key =
            "e9a815ab9d6e86abbf33a4ac64e9196d5be44a09bd0ed6ae052914e1a865ac8331fed863de8ea697e9a7f63329e5e23cda09c72570f46775b7e39ea9670086f847d3c9c51963b131409b1e04265d9747419c635404ca651bbcbc87f99b8008f7f5824653e3658be4ba73e4480156b390bb73bc1f8b33578e7a4e12440e9396f2552c1aff1c92e797ebacdc37c109ab7bce2367a19c56a033ee04534723cc2558cb27368f5b9d32c04d12dbd86bbd68b1d99b7c349a8453ea75d1b2e94491ab30acf6c46a36a75b721b312bedf4e7aad21e54e9bcbcf8144c79b6e3c05eb4a1547750d224c0085d80e6da3907c3d945051c13c7c1dcefd6520ee8379c4f5231ed";
        mod = "10001";
        var _RSA = new RSAKey;
        _RSA.setPublic(key, mod);
        return _RSA.encrypt(rawValue)
    }

    return {
        rsa_encrypt: rsa_encrypt
    }
}();


function __encrypt(data) {
    __plain = new Array(8);
    __prePlain = new Array(8);
    __cryptPos = __preCryptPos = 0;
    __header = true;
    __pos = 0;
    var len = data.length;
    var padding = 0;
    __pos = (len + 10) % 8;
    if (__pos != 0) __pos = 8 - __pos;
    __out = new Array(len + __pos + 10);
    __plain[0] = (__rand() & 248 | __pos) & 255;
    for (var i = 1; i <= __pos; i++) {
        __plain[i] = __rand() & 255
    }
    __pos++;
    for (var i = 0; i < 8; i++) {
        __prePlain[i] = 0
    }
    padding = 1;
    while (padding <= 2) {
        if (__pos < 8) {
            __plain[__pos++] = __rand() & 255;
            padding++
        }
        if (__pos == 8) __encrypt8bytes()
    }
    var i = 0;
    while (len > 0) {
        if (__pos < 8) {
            __plain[__pos++] = data[i++];
            len--
        }
        if (__pos == 8) __encrypt8bytes()
    }
    padding = 1;
    while (padding <= 7) {
        if (__pos < 8) {
            __plain[__pos++] = 0;
            padding++
        }
        if (__pos == 8) __encrypt8bytes()
    }
    return __out
}

function __encipher(data) {
    var loop = 16;
    var y = __getUInt(data, 0, 4);
    var z = __getUInt(data, 4, 4);
    var a = __getUInt(__key, 0, 4);
    var b = __getUInt(__key, 4, 4);
    var c = __getUInt(__key, 8, 4);
    var d = __getUInt(__key, 12, 4);
    var sum = 0;
    var delta = 2654435769 >>> 0;
    while (loop-- > 0) {
        sum += delta;
        sum = (sum & 4294967295) >>> 0;
        y += (z << 4) + a ^ z + sum ^ (z >>> 5) + b;
        y = (y & 4294967295) >>> 0;
        z += (y << 4) + c ^ y + sum ^ (y >>> 5) + d;
        z = (z & 4294967295) >>> 0
    }
    var bytes = new Array(8);
    __intToBytes(bytes, 0, y);
    __intToBytes(bytes, 4, z);
    return bytes
}

function __monitor(mid, probability) {
    if (Math.random() > (probability || 1)) return;
    try {
        var url = location.protocol + "//ui.ptlogin2.qq.com/cgi-bin/report?id=" + mid;
        var s = document.createElement("img");
        s.src = url
    } catch (e) {
    }
}

function __getUInt(data, offset, len) {
    if (!len || len > 4) len = 4;
    var ret = 0;
    for (var i = offset; i < offset + len; i++) {
        ret <<= 8;
        ret |= data[i]
    }
    return (ret & 4294967295) >>> 0
}

function __rand() {
    return Math.round(Math.random() * 4294967295)
}

function __intToBytes(data, offset, value) {
    data[offset + 3] = value >> 0 & 255;
    data[offset + 2] = value >> 8 & 255;
    data[offset + 1] = value >> 16 & 255;
    data[offset + 0] = value >> 24 & 255
}

function __encrypt8bytes() {
    for (var i = 0; i < 8; i++) {
        if (__header) __plain[i] ^= __prePlain[i];
        else __plain[i] ^= __out[__preCryptPos + i]
    }
    var crypted = __encipher(__plain);
    for (var i = 0; i < 8; i++) {
        __out[__cryptPos + i] = crypted[i] ^ __prePlain[i];
        __prePlain[i] = __plain[i]
    }
    __preCryptPos = __cryptPos;
    __cryptPos += 8;
    __pos = 0;
    __header = false
}

function __bytesInStr(data) {
    if (!data) return "";
    var outInHex = "";
    for (var i = 0; i < data.length; i++) {
        var hex = Number(data[i]).toString(16);
        if (hex.length == 1) hex = "0" + hex;
        outInHex += hex
    }
    return outInHex
}

function __bytesToStr(data) {
    var str = "";
    for (var i = 0; i < data.length; i += 2) {
        str += String.fromCharCode(parseInt(data.substr(i, 2), 16))
    }
    return str
}

function __strToBytes(str, uincode) {
    if (!str) return "";
    if (uincode) str = utf16ToUtf8(str);
    var data = [];
    for (var i = 0; i < str.length; i++) {
        data[i] = str.charCodeAt(i)
    }
    return __bytesInStr(data)
}

function __dataFromStr(str, isASCII) {
    var data = [];
    if (isASCII) {
        for (var i = 0; i < str.length; i++) {
            data[i] = str.charCodeAt(i) & 255
        }
    } else {
        var k = 0;
        for (var i = 0; i < str.length; i += 2) {
            data[k++] = parseInt(str.substr(i, 2), 16)
        }
    }
    return data
}

function uin2hex(str) {
    var maxLength = 16;
    str = parseInt(str);
    var hex = str.toString(16);
    var len = hex.length;
    for (var i = len; i < maxLength; i++) {
        hex = "0" + hex
    }
    var arr = [];
    for (var j = 0; j < maxLength; j += 2) {
        arr.push("\\x" + hex.substr(j, 2))
    }
    var result = arr.join("");
    eval("result=\"" + result + "\"");
    return result
}

function utf16ToUtf8(s) {
    var i, code, ret = [],
        len = s.length;
    for (i = 0; i < len; i++) {
        code = s.charCodeAt(i);
        if (code > 0 && code <= 127) {
            ret.push(s.charAt(i))
        } else if (code >= 128 && code <= 2047) {
            ret.push(String.fromCharCode(192 | code >> 6 & 31), String.fromCharCode(128 | code & 63))
        } else if (code >= 2048 && code <= 65535) {
            ret.push(String.fromCharCode(224 | code >> 12 & 15), String.fromCharCode(128 | code >> 6 & 63), String.fromCharCode(
                128 | code & 63))
        }
    }
    return ret.join("")
}

var chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';

function btoa(input) {
    var str = String(input);
    for (
        // initialize result and counter
        var block, charCode, idx = 0, map = chars, output = '';
        // if the next str index does not exist:
        //   change the mapping table to "="
        //   check if d has no fractional digits
        str.charAt(idx | 0) || (map = '=', idx % 1);
        // "8 - idx % 1 * 8" generates the sequence 2, 4, 6, 8
        output += map.charAt(63 & block >> 8 - idx % 1 * 8)
    ) {
        charCode = str.charCodeAt(idx += 3 / 4);
        if (charCode > 0xFF) {
            throw new InvalidCharacterError("'btoa' failed: The string to be encoded contains characters outside of the Latin1 range.");
        }
        block = block << 8 | charCode;
    }
    return output;
}

var TEA = {
    encrypt: function encrypt(str, isASCII) {
        var data = __dataFromStr(str, isASCII);
        var encrypted = __encrypt(data);
        return __bytesInStr(encrypted)
    },
    enAsBase64: function enAsBase64(str, isASCII) {
        var data = __dataFromStr(str, isASCII);
        var encrypted = __encrypt(data);
        var bytes = "";
        for (var i = 0; i < encrypted.length; i++) {
            bytes += String.fromCharCode(encrypted[i])
        }
        return btoa(bytes)
    },
    decrypt: function decrypt(str) {
        var data = __dataFromStr(str, false);
        var decrypted = __decrypt(data);
        return __bytesInStr(decrypted)
    },
    initkey: function initkey(key, isASCII) {
        __key = __dataFromStr(key, isASCII)
    },

    bytesToStr: __bytesToStr,
    strToBytes: __strToBytes,
    bytesInStr: __bytesInStr,
    dataFromStr: __dataFromStr
};
function getEncryption(password, salt, vcode, isMd5) {
    vcode = vcode || "";
    password = password || "";
    var md5Pwd = isMd5 ? password : md5(password),
        h1 = hexchar2bin(md5Pwd),
        s2 = md5(h1 + salt),
        hexVcode = TEA.strToBytes(vcode.toUpperCase(), true),
        vcodeLen = Number(hexVcode.length / 2).toString(16);
    while (vcodeLen.length < 4) {
        vcodeLen = "0" + vcodeLen
    }
    TEA.initkey(s2);
    var rawContent = TEA.encrypt(md5Pwd + TEA.strToBytes(salt) + vcodeLen + hexVcode);
    TEA.initkey("");
    var rawLength = Number(rawContent.length / 2).toString(16);
    while (rawLength.length < 4) {
        rawLength = "0" + rawLength
    }
    var result = RSA.rsa_encrypt(hexchar2bin(rawLength + rawContent));
    return btoa(hexchar2bin(result)).replace(/[\/\+=]/g, function (a) {
        return {
            "/": "-",
            "+": "*",
            "=": "_"
        } [a]
    })
}


function hexchar2bin(str) {
    var arr = [];
    for (var i = 0; i < str.length; i = i + 2) {
        arr.push(String.fromCharCode(parseInt(str.substr(i, 2), 16)))
    }
    return arr.join("")
}

var hexcase = 1;
var b64pad = "";
var chrsz = 8;
var mode = 32;

function md5(s) {
    return hex_md5(s)
}

function hex_md5(s) {
    return binl2hex(core_md5(str2binl(s), s.length * chrsz))
}

function str_md5(s) {
    return binl2str(core_md5(str2binl(s), s.length * chrsz))
}

function hex_hmac_md5(key, data) {
    return binl2hex(core_hmac_md5(key, data))
}

function b64_hmac_md5(key, data) {
    return binl2b64(core_hmac_md5(key, data))
}

function str_hmac_md5(key, data) {
    return binl2str(core_hmac_md5(key, data))
}

function core_md5(x, len) {
    x[len >> 5] |= 128 << len % 32;
    x[(len + 64 >>> 9 << 4) + 14] = len;
    var a = 1732584193;
    var b = -271733879;
    var c = -1732584194;
    var d = 271733878;
    for (var i = 0; i < x.length; i += 16) {
        var olda = a;
        var oldb = b;
        var oldc = c;
        var oldd = d;
        a = md5_ff(a, b, c, d, x[i + 0], 7, -680876936);
        d = md5_ff(d, a, b, c, x[i + 1], 12, -389564586);
        c = md5_ff(c, d, a, b, x[i + 2], 17, 606105819);
        b = md5_ff(b, c, d, a, x[i + 3], 22, -1044525330);
        a = md5_ff(a, b, c, d, x[i + 4], 7, -176418897);
        d = md5_ff(d, a, b, c, x[i + 5], 12, 1200080426);
        c = md5_ff(c, d, a, b, x[i + 6], 17, -1473231341);
        b = md5_ff(b, c, d, a, x[i + 7], 22, -45705983);
        a = md5_ff(a, b, c, d, x[i + 8], 7, 1770035416);
        d = md5_ff(d, a, b, c, x[i + 9], 12, -1958414417);
        c = md5_ff(c, d, a, b, x[i + 10], 17, -42063);
        b = md5_ff(b, c, d, a, x[i + 11], 22, -1990404162);
        a = md5_ff(a, b, c, d, x[i + 12], 7, 1804603682);
        d = md5_ff(d, a, b, c, x[i + 13], 12, -40341101);
        c = md5_ff(c, d, a, b, x[i + 14], 17, -1502002290);
        b = md5_ff(b, c, d, a, x[i + 15], 22, 1236535329);
        a = md5_gg(a, b, c, d, x[i + 1], 5, -165796510);
        d = md5_gg(d, a, b, c, x[i + 6], 9, -1069501632);
        c = md5_gg(c, d, a, b, x[i + 11], 14, 643717713);
        b = md5_gg(b, c, d, a, x[i + 0], 20, -373897302);
        a = md5_gg(a, b, c, d, x[i + 5], 5, -701558691);
        d = md5_gg(d, a, b, c, x[i + 10], 9, 38016083);
        c = md5_gg(c, d, a, b, x[i + 15], 14, -660478335);
        b = md5_gg(b, c, d, a, x[i + 4], 20, -405537848);
        a = md5_gg(a, b, c, d, x[i + 9], 5, 568446438);
        d = md5_gg(d, a, b, c, x[i + 14], 9, -1019803690);
        c = md5_gg(c, d, a, b, x[i + 3], 14, -187363961);
        b = md5_gg(b, c, d, a, x[i + 8], 20, 1163531501);
        a = md5_gg(a, b, c, d, x[i + 13], 5, -1444681467);
        d = md5_gg(d, a, b, c, x[i + 2], 9, -51403784);
        c = md5_gg(c, d, a, b, x[i + 7], 14, 1735328473);
        b = md5_gg(b, c, d, a, x[i + 12], 20, -1926607734);
        a = md5_hh(a, b, c, d, x[i + 5], 4, -378558);
        d = md5_hh(d, a, b, c, x[i + 8], 11, -2022574463);
        c = md5_hh(c, d, a, b, x[i + 11], 16, 1839030562);
        b = md5_hh(b, c, d, a, x[i + 14], 23, -35309556);
        a = md5_hh(a, b, c, d, x[i + 1], 4, -1530992060);
        d = md5_hh(d, a, b, c, x[i + 4], 11, 1272893353);
        c = md5_hh(c, d, a, b, x[i + 7], 16, -155497632);
        b = md5_hh(b, c, d, a, x[i + 10], 23, -1094730640);
        a = md5_hh(a, b, c, d, x[i + 13], 4, 681279174);
        d = md5_hh(d, a, b, c, x[i + 0], 11, -358537222);
        c = md5_hh(c, d, a, b, x[i + 3], 16, -722521979);
        b = md5_hh(b, c, d, a, x[i + 6], 23, 76029189);
        a = md5_hh(a, b, c, d, x[i + 9], 4, -640364487);
        d = md5_hh(d, a, b, c, x[i + 12], 11, -421815835);
        c = md5_hh(c, d, a, b, x[i + 15], 16, 530742520);
        b = md5_hh(b, c, d, a, x[i + 2], 23, -995338651);
        a = md5_ii(a, b, c, d, x[i + 0], 6, -198630844);
        d = md5_ii(d, a, b, c, x[i + 7], 10, 1126891415);
        c = md5_ii(c, d, a, b, x[i + 14], 15, -1416354905);
        b = md5_ii(b, c, d, a, x[i + 5], 21, -57434055);
        a = md5_ii(a, b, c, d, x[i + 12], 6, 1700485571);
        d = md5_ii(d, a, b, c, x[i + 3], 10, -1894986606);
        c = md5_ii(c, d, a, b, x[i + 10], 15, -1051523);
        b = md5_ii(b, c, d, a, x[i + 1], 21, -2054922799);
        a = md5_ii(a, b, c, d, x[i + 8], 6, 1873313359);
        d = md5_ii(d, a, b, c, x[i + 15], 10, -30611744);
        c = md5_ii(c, d, a, b, x[i + 6], 15, -1560198380);
        b = md5_ii(b, c, d, a, x[i + 13], 21, 1309151649);
        a = md5_ii(a, b, c, d, x[i + 4], 6, -145523070);
        d = md5_ii(d, a, b, c, x[i + 11], 10, -1120210379);
        c = md5_ii(c, d, a, b, x[i + 2], 15, 718787259);
        b = md5_ii(b, c, d, a, x[i + 9], 21, -343485551);
        a = safe_add(a, olda);
        b = safe_add(b, oldb);
        c = safe_add(c, oldc);
        d = safe_add(d, oldd)
    }
    if (mode == 16) {
        return Array(b, c)
    } else {
        return Array(a, b, c, d)
    }
}

function md5_cmn(q, a, b, x, s, t) {
    return safe_add(bit_rol(safe_add(safe_add(a, q), safe_add(x, t)), s), b)
}

function md5_ff(a, b, c, d, x, s, t) {
    return md5_cmn(b & c | ~b & d, a, b, x, s, t)
}

function md5_gg(a, b, c, d, x, s, t) {
    return md5_cmn(b & d | c & ~d, a, b, x, s, t)
}

function md5_hh(a, b, c, d, x, s, t) {
    return md5_cmn(b ^ c ^ d, a, b, x, s, t)
}

function md5_ii(a, b, c, d, x, s, t) {
    return md5_cmn(c ^ (b | ~d), a, b, x, s, t)
}

function core_hmac_md5(key, data) {
    var bkey = str2binl(key);
    if (bkey.length > 16) bkey = core_md5(bkey, key.length * chrsz);
    var ipad = Array(16),
        opad = Array(16);
    for (var i = 0; i < 16; i++) {
        ipad[i] = bkey[i] ^ 909522486;
        opad[i] = bkey[i] ^ 1549556828
    }
    var hash = core_md5(ipad.concat(str2binl(data)), 512 + data.length * chrsz);
    return core_md5(opad.concat(hash), 512 + 128)
}

function safe_add(x, y) {
    var lsw = (x & 65535) + (y & 65535);
    var msw = (x >> 16) + (y >> 16) + (lsw >> 16);
    return msw << 16 | lsw & 65535
}

function bit_rol(num, cnt) {
    return num << cnt | num >>> 32 - cnt
}

function str2binl(str) {
    var bin = Array();
    var mask = (1 << chrsz) - 1;
    for (var i = 0; i < str.length * chrsz; i += chrsz) {
        bin[i >> 5] |= (str.charCodeAt(i / chrsz) & mask) << i % 32
    }
    return bin
}

function binl2str(bin) {
    var str = "";
    var mask = (1 << chrsz) - 1;
    for (var i = 0; i < bin.length * 32; i += chrsz) {
        str += String.fromCharCode(bin[i >> 5] >>> i % 32 & mask)
    }
    return str
}

function binl2hex(binarray) {
    var hex_tab = hexcase ? "0123456789ABCDEF" : "0123456789abcdef";
    var str = "";
    for (var i = 0; i < binarray.length * 4; i++) {
        str += hex_tab.charAt(binarray[i >> 2] >> i % 4 * 8 + 4 & 15) + hex_tab.charAt(binarray[i >> 2] >> i % 4 * 8 & 15)
    }
    return str
}

function binl2b64(binarray) {
    var tab = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    var str = "";
    for (var i = 0; i < binarray.length * 4; i += 3) {
        var triplet = (binarray[i >> 2] >> 8 * (i % 4) & 255) << 16 | (binarray[i + 1 >> 2] >> 8 * ((i + 1) % 4) & 255) <<
            8 | binarray[i + 2 >> 2] >> 8 * ((i + 2) % 4) & 255;
        for (var j = 0; j < 4; j++) {
            if (i * 8 + j * 6 > binarray.length * 32) str += b64pad;
            else str += tab.charAt(triplet >> 6 * (3 - j) & 63)
        }
    }
    return str
}

function hexchar2bin(str) {
    var arr = [];
    for (var i = 0; i < str.length; i = i + 2) {
        arr.push(String.fromCharCode(parseInt(str.substr(i, 2), 16)))
    }
    return arr.join("")
}
console.log(getUrl(...process.argv.slice(2)));