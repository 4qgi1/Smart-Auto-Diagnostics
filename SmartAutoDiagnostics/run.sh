#!/usr/bin/env bash
# تشغيل المشروع:
#   ./run.sh         يجمّع ويشغّل البرنامج
#   ./run.sh test    يجمّع ويشغّل الاختبارات الذاتية
#   ./run.sh docs    يعيد توليد مخططات الشجرة في docs/decision-trees.md
set -e
cd "$(dirname "$0")"

rm -rf out
mkdir -p out
javac -encoding UTF-8 -d out $(find src test -name "*.java")

case "${1:-run}" in
  test) java -cp out com.smartauto.SelfTest ;;
  docs) java -cp out com.smartauto.tools.DocsGenerator docs/decision-trees.md ;;
  *)    java -cp out com.smartauto.Main ;;
esac
