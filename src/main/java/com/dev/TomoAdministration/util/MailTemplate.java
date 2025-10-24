package com.dev.TomoAdministration.util;


public class MailTemplate {

    // 제목은 개행 없이 한 줄로 권장
    public static String subject() {
        return "【重要】SNSTOMO公式LINEが新しくなりました！ 今だけチャージ額＋10％増量キャンペーン実施中🎁";
    }

    public static String buildHtml(String usernameNullable) {
        String greet = (usernameNullable != null && !usernameNullable.trim().isEmpty())
                ? "<p style='margin:0 0 16px;'>" + escape(usernameNullable.trim()) + " 様</p>"
                : "";

        StringBuilder sb = new StringBuilder();
        sb.append("<div style=\"font-family:'Noto Sans JP',sans-serif; font-size:15px; line-height:1.8; color:#111;\">")
          .append("<div style=\"text-align:center; margin-bottom:16px;\">")
          .append("<img src=\"cid:banner\" alt=\"SNSTOMO\" style=\"max-width:100%; height:auto;\"/>")
          .append("</div>")
          .append(greet)
          .append("<p>いつもSNSTOMOをご利用いただきありがとうございます。</p>")
          .append("<p>このたび、SNSTOMO公式LINEが新しくなりました！<br/>")
          .append("今後は新しいLINEアカウントから、最新情報やお得なキャンペーンをお届けいたします！</p>")
          .append("<p>現在、LINE移行を記念して<strong>【チャージ額10％増量キャンペーン】</strong>を実施中です🎉</p>")
          .append("<hr style=\"border:none; border-top:1px solid #ddd; margin:16px 0;\"/>")
          .append("<p><strong>■ キャンペーン内容</strong><br/>")
          .append("新しいSNSTOMO公式LINEを追加し、下記の手順でメッセージをお送りいただくと、")
          .append("チャージ額の10％を追加で増量いたします！<br/>")
          .append("※ご利用はお一人様1回限りとなります。</p>")
          .append("<p><strong>■ 参加方法</strong></p>")
          .append("<ol style=\"padding-left:20px;\">")
          .append("<li>SNSTOMOの新しい公式LINEを追加<br/>LINE ID：<strong>@989rojzx</strong></li>")
          .append("<li>SNSTOMO公式ホームページにてチャージを購入</li>")
          .append("<li>LINEトーク画面で以下の内容を送信<br/>")
          .append("・SNSTOMO ID<br/>")
          .append("・チャージ購入日時<br/>")
          .append("・「チャージ増量キャンペーン参加」</li>")
          .append("</ol>")
          .append("<p>スタッフが内容を確認後、キャンペーン特典を適用いたします。</p>")
          .append("<hr style=\"border:none; border-top:1px solid #ddd; margin:16px 0;\"/>")
          .append("<p>LINEを追加するだけで簡単にご参加いただけます。<br/>")
          .append("ぜひこの機会に新しいLINEを追加のうえ、ご利用ください。</p>")
          .append("<p>※本キャンペーンは予告なく終了する場合がございます。お早めのご参加をおすすめいたします。</p>")
          .append("<p>今後ともSNSTOMOをよろしくお願いいたします。</p>")
          .append("<hr style=\"border:none; border-top:1px solid #ddd; margin:16px 0;\"/>")
          .append("<p>SNSTOMO運営事務局</p>")
          .append("</div>");
        return sb.toString();
    }

    private static String escape(String s) {
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }
}
